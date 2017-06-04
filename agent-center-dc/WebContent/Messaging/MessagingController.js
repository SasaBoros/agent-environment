var messagingModule = angular.module('messagingModule');

messagingModule.controller('messageController', ['$scope', function($scope) {
	
	$scope.socket = new WebSocket("ws://localhost:8080/agent-center-dc/agent-center");
	$scope.communication = {type : "RS"};
	
	$scope.socket.onopen = function(message) {
 		console.log("Connection opened");
 	}
	
	$scope.socket.onmessage = function(message) {
		console.log(message);
 	}
 	
	$scope.socket.onclose = function(message) {
		console.log("Connection closed");
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

messagingModule.service('communicationService', function() {
});