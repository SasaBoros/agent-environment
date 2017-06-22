
messagingModuleWS.service('wsMessageService', function() {
	
	this.handleWSMessage = function(data, wsMessage) {
		var message = angular.fromJson(wsMessage);
		var messageType = message.messageType;
		var content = angular.fromJson(message.content);
		if(messageType == 'PERFORMATIVES') {
			data.performatives = content;
		}
		else if(messageType == 'AGENT_TYPES') {
			data.agentTypes = content;
		}
		else if(messageType == 'RUNNING_AGENTS') {
			console.log(content)
			for(var i = 0; i < content.length;i++) {
				for(var j = 0; j < data.runningAgents.length; j++) {
					if(content[i].id.name == data.runningAgents[j].id.name) {
						content[i] = data.runningAgents[j];
					}
					
				}
			}
			data.runningAgents = content;
		}
		else if(messageType == 'STARTED_AGENT') {
			data.runningAgents.push(content);
		}
		else if(messageType == 'DELETED_AGENT') {
			for(var i = 0; i < data.runningAgents.length; i++) {
				if(data.runningAgents[i].id.name == content) {
					data.runningAgents.splice(i, 1);
					return;
				}
			}
		}
		else if(messageType == 'ERROR') {
			toastr.error(content);
		}
		else if(messageType == 'ERROR_FREE') {
			toastr.info(content);
		}
	}
	
	this.sendWSMessage = function(socket, type, content) {
		var wsMessage = {'messageType' : type, 'content' : content};
		socket.send(angular.toJson(wsMessage));
	}
	
	this.sendMessage = function(socket, message) {
		if(message.performative == "" || message.sender == null || message.receivers.length == 0 || message.replyTo == null || message.content == "" 
			|| message.language == "" || message.encoding == "" || message.ontology == "" || message.protocol == "" || message.conversationId == "" 
					|| message.replyWith == "" || message.inReplyTo == "" || message.replyBy == null) {
			toastr.warning("All fields are required.");
			return;
		}
		
		this.sendWSMessage(socket, 'MESSAGE', angular.toJson(message));
	}
	
	this.startAgent = function(socket, agent) {
		if(agent.name == null || agent.name == "") {
			toastr.warning("Agent name must be choosen.");
			return;
		}
		this.sendWSMessage(socket, 'START_AGENT', agent.type + "/" + agent.name);
		agent.name = "";
	}
	
	this.stopAgent = function(socket, agentName) {
		this.sendWSMessage(socket, 'STOP_AGENT', agentName);
		
	}
});
