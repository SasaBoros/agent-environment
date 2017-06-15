var rsMessagingModule = angular.module('rsMessagingModule', []);

rsMessagingModule.controller('rsMessageController', [ '$scope', '$interval',
		'rsResourceService', 'rsMessageService', 'rsAgentService',
		function($scope, $interval, rsResourceService, rsMessageService, rsAgentService) {
			
			$scope.agent = {type : "", name: ""};
			$scope.data = {performatives : new Array(), agentTypes : new Array(), runningAgents : new Array()};
			
			var intervalInMS = 10000;
			
			$scope.message = {
					performative : "",
					sender : null,
					receivers : [],
					replyTo : null,
					content : "",
					language : "",
					encoding : "",
					ontology : "",
					protocol : "",
					conversationId : "",
					replyWith : "",
					inReplyTo : "",
					replyBy : null
				};
			
			
			var getPerformatives = function() {
				rsResourceService.getPerformatives().then(function(response) {
					if(response.status == -1)
						$interval.cancel($scope.performativeInterval);
					$scope.data.performatives = response.data;
				});
				
			}
			var getAgentTypes = function() {
				rsResourceService.getAgentTypes().then(function(response) {
					if(response.status == -1)
						$interval.cancel($scope.agentTypesInterval);
					$scope.data.agentTypes = response.data;
				});
			}
			
			var getRunningAgents = function() {
				rsResourceService.getRunningAgents().then(function(response) {
					if(response.status == -1)
						$interval.cancel($scope.runningAgentsInterval);
					for(var i = 0; i < response.data.length;i++) {
						for(var j = 0; j < $scope.data.runningAgents.length; j++) {
							if(response.data[i].id.name == $scope.data.runningAgents[j].id.name) {
								response.data[i] = $scope.data.runningAgents[j];
							}
							
						}
					}
					$scope.data.runningAgents = response.data;
				});
			}
			
			getPerformatives();
			getAgentTypes();
			getRunningAgents();
			
			$scope.performativeInterval = $interval(getPerformatives, intervalInMS);
			$scope.agentTypesInterval = $interval(getAgentTypes, intervalInMS);
			$scope.runningAgentsInterval = $interval(getRunningAgents, intervalInMS);
			

			$scope.sendMessage = function() {
				rsMessageService.sendMessage($scope.message);
			}
			
			$scope.startAgent = function() {
				rsAgentService.startAgent($scope.agent);
				$scope.data.runningAgents.push({id : {name : $scope.agent.name}})
			}
			
			$scope.stopAgent = function(agentName) {
				rsAgentService.stopAgent($scope.data.runningAgents, agentName);
				
			}
			

}]);