var messagingModuleWS = angular.module('wsMessagingModule', []);

messagingModuleWS.controller('wsMessageController', ['$scope', 'wsMessageService', function($scope, wsMessageService) {
	
	$scope.$parent.protocol = 'ws';
	
	$scope.data = {performatives : new Array(), agentTypes : new Array(), runningAgents : new Array()};
	
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
	
	$scope.socket = new WebSocket('ws://localhost:8080/agent-center-dc/agent-center');
	
	$scope.socket.onopen = function(message) {
	 		console.log('Connection opened');
	 	}
		
	$scope.socket.onmessage = function(message) {
			wsMessageService.handleWSMessage($scope.data, message.data);
			$scope.$apply();
	 	}
	 	
	$scope.socket.onclose = function(message) {
			console.log('Connection closed');
	 	}
	 	
	$scope.socket.onerror = function(message) {
			console.log(message);
	 	}
	
	$scope.sendMessage = function() {
		wsMessageService.sendMessage($scope.socket, $scope.message);
	}
	
	$scope.startAgent = function() {
		wsMessageService.startAgent($scope.socket, $scope.agent);
	}
	
	$scope.stopAgent = function(agentName) {
		wsMessageService.stopAgent($scope.socket, $scope.data, agentName);
	}
	
}]);


