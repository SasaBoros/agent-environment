var wsMessagingModule = angular.module('messagingModule-ws', []);

wsMessagingModule.controller('messageController-ws', ['$scope', function($scope) {
	
	$scope.socket = new WebSocket('ws://localhost:8080/agent-center-dc/agent-center');
	$scope.communication = {'type' : 'RS'};
	$scope.wsMessage = {'type' : '', 'content' : {}};
	$scope.performatives = [];
	
	$scope.socket.onopen = function(message) {
 		console.log('Connection opened');
 	}
	
	$scope.socket.onmessage = function(message) {
		console.log(message);
		if(message.type='load')
		performatives
 	}
 	
	$scope.socket.onclose = function(message) {
		console.log('Connection closed');
 	}
 	
	$scope.socket.onerror = function(message) {
		console.log(message);
 	}
	
	$scope.publish = function() {
 		$scope.socket.send($scope.message);
 	}
	
	$scope.sendMessage = function() {
		if($scope.communication.type == 'RS') {
			restMessageService.send($scope.message);
		}
		else if($scope.communication.type == 'WS') {
			websocketMessageService.send($scope.message);
		}
	}
	
	$scope.createAgent = function() {
		
	}
	
}]);

wsMessagingModule.service('communicationService', function() {
});