var rsMessagingModule = angular.module('rsMessagingModule', []);

rsMessagingModule.controller('rsMessageController', [ '$scope',
		'rsResourceService', 'rsMessageService', 'rsAgentService',
		function($scope, rsResourceService, rsMessageService, rsAgentService) {

			rsResourceService.getPerformatives().then(function(data) {
				$scope.performatives = data;
			});

			rsResourceService.getAgentTypes().then(function(data) {
				$scope.agentTypes = data;
			});

			rsResourceService.getRunningAgents().then(function(data) {
				$scope.runningAgents = data;
			});

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
			
			$scope.agent = {type : "", name: ""};

			$scope.sendMessage = function() {
				rsMessageService.sendMessage($scope.message);
			}
			
			$scope.startAgent = function() {
				rsAgentService.startAgent($scope.agent);
				$scope.agent.name = "";
			}
			
			$scope.stopAgent = function(agentName) {
				rsAgentService.stopAgent(agentName);
			}

		} ]);