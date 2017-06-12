rsMessagingModule.service('rsResourceService', ['$http', function($http) {
	
	this.getPerformatives = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/agents/performatives'
			}).then(function successCallback(response) {
					return response.data;
			  }, function errorCallback(response) {
				  console.log(response);
			  });
	}
	
	this.getAgentTypes = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/agents/types'
			}).then(function successCallback(response) {
				return response.data;
			  }, function errorCallback(response) {
				  console.log(response);
			  });
	}
	
	this.getRunningAgents = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/agents/running-agents'
			}).then(function successCallback(response) {
				return response.data;
			  }, function errorCallback(response) {
				  console.log(response);
			  });
		}
	
}]);

rsMessagingModule.service('rsMessageService', ['$http', function($http) { 
	this.sendMessage = function(message) {
		if(message.performative == "" || message.sender == null || message.receivers.length == 0 || message.replyTo == null || message.content == "" 
			|| message.language == "" || message.encoding == "" || message.ontology == "" || message.protocol == "" || message.conversationId == "" 
					|| message.replyWith == "" || message.inReplyTo == "" || message.replyBy == null) {
			toastr.warning("All fields are required.");
			return;
		}
		$http({
			  method: 'POST',
			  url: '../agent-center-dc/rest/agent-center/message',
			  data: message
			}).then(function successCallback(response) {
				
			  }, function errorCallback(response) {
				  
			  });
		}
}]);

rsMessagingModule.service('rsAgentService', ['$http', function($http) { 
	this.startAgent = function(agent) {
		$http({
			  method: 'PUT',
			  url: '../agent-center-dc/rest/agent-center/agents/agent/start/' + agent.type + "/" + agent.name
			}).then(function successCallback(response) {
				
			  }, function errorCallback(response) {
				  
			  });
	}
	
	this.stopAgent = function(agentName) {
		$http({
			  method: 'DELETE',
			  url: '../agent-center-dc/rest/agent-center/agents/agent/stop/' + agentName
			}).then(function successCallback(response) {
				
			  }, function errorCallback(response) {
				  
			  });
	}
}]);