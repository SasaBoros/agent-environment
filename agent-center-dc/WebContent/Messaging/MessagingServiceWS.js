
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
			console.log(data.agentTypes)
		}
		else if(messageType == 'RUNNING_AGENTS') {
			for(var i = 0; i < content.length;i++) {
				for(var j = 0; j < data.runningAgents.length; j++) {
					if(content[i].id.name == data.runningAgents[j].id.name) {
						content[i] = data.runningAgents[j];
					}
					
				}
			}
			data.runningAgents = content;
			console.log(data.runningAgents)
		}
		else if(messageType == 'STARTED_AGENT') {
			data.runningAgents.push(content);
		}
		else if(messageType == 'ERROR') {
			toastr.error(content);
		}
	}
	
	this.sendWSMessage = function(socket, type, content) {
		var wsMessage = {'messageType' : type, 'content' : angular.toJson(content)};
		socket.send(angular.toJson(wsMessage));
	}
	
	this.sendMessage = function(socket, message) {
		if(message.performative == "" || message.sender == null || message.receivers.length == 0 || message.replyTo == null || message.content == "" 
			|| message.language == "" || message.encoding == "" || message.ontology == "" || message.protocol == "" || message.conversationId == "" 
					|| message.replyWith == "" || message.inReplyTo == "" || message.replyBy == null) {
			toastr.warning("All fields are required.");
			return;
		}
		
		this.sendWSMessage(socket, 'MESSAGE', message);
	}
	
	this.startAgent = function(socket, agent) {
		this.sendWSMessage(socket, 'START_AGENT', agent.name + "/" + agent.type);
		agent.name = "";
	}
	
	this.stopAgent = function(socket, data, agentName) {
		this.sendWSMessage(socket, 'STOP_AGENT', agentName);
		for(var i = 0;i < data.runningAgents.length; i++) {
			if(data.runningAgents[i].id.name == agentName) {
				data.runningAgents.splice(i, 1);
				break;
			}
		}
	}
});
