
messagingModuleWS.service('wsMessageService', ['wsAgentService', function(wsAgentService) {
	
	this.socket = new WebSocket('ws://localhost:8080/agent-center-dc/agent-center');
	
	this.createConnection = function() {
		this.socket.onopen = function(message) {
	 		console.log('Connection opened');
	 	}
		
		this.socket.onmessage = function(message) {
			console.log(message);
			wsMessageService.handleWSMessage(message);
	 	}
	 	
		this.socket.onclose = function(message) {
			console.log('Connection closed');
	 	}
	 	
		this.socket.onerror = function(message) {
			console.log(message);
	 	}
	}
	
	this.handleWSMessage = function(wsMessage) {
		if(wsMessage.responseType == 'PERFOMATIVES') {
			wsAgentService.performatives = JSON.parse(wsMessage.content);
		}
		else if(wsMessage.responseType == 'AGENTS') {
			wsAgentService.agents = JSON.parse(wsMessage.content);
		}
		else if(wsMessage.responseType == 'RUNNING-AGENTS') {
			wsAgentService.runningAgents = JSON.parse(wsMessage.content);
		}
	}
	
	this.sendWSMessage = function(type, content) {
		var wsMessage = {'type' : type, 'content' : content};
		websocketService.socket.send(JSON.stringify(wsMessage));
	}
}]);

messagingModuleWS.service('agentServiceWS', function() {
	this.performatives = [];
	this.agents = [];
	this.runningAgents = [];
});