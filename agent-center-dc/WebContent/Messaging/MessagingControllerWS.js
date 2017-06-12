var messagingModuleWS = angular.module('wsMessagingModule', []);

messagingModuleWS.controller('wsMessageController', ['$scope', 'wsMessageService', 'wsAgentService', function($scope, wsMessageService, wsAgentService) {
	
	wsMessageService.createConnection();
	$scope.performatives = wsAgentService.performatives;
	$scope.agents = wsAgentService.agents;
	$scope.runningAgents = wsAgentService.runningAgents;
	
	$scope.newAgent = {}
	$scope.message = {}
	
	$scope.sendACLMessage = function() {
		wsMessageService.send('MESSAGE', $scope.message);
	}
	
	$scope.startAgent = function() {
		wsMessageService.send('AGENT', $scope.newAgent);
	}
	
}]);


