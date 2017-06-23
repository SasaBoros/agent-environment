rsMessagingModule.service('rsResourceService', ['$http', function($http) {
	
	this.getPerformatives = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/message/performatives'
			}).then(function successCallback(response) {
				return response;
			  }, function errorCallback(response) {
				  return response;
			  });
	}
	
	this.getAgentTypes = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/agent-type/agent-types'
			}).then(function successCallback(response) {
				return response;
			  }, function errorCallback(response) {
				  return response;
			  });
	}
	
	this.getRunningAgents = function() {
		return $http({
			  method: 'GET',
			  url: '../agent-center-dc/rest/agent-center/agent/running-agents'
			}).then(function successCallback(response) {
				return response;
			  }, function errorCallback(response) {
				  return response;
			  });
		}
	
}]);

rsMessagingModule.service('rsMessageService', ['$http', function($http) { 
	this.sendMessage = function(message) {
		if(message.performative == "" || message.receivers.length == 0) {
			toastr.warning("Performative and recievers are mandatory.");
			return;
		}
		var self = this;
		$http({
			  method: 'POST',
			  url: '../agent-center-dc/rest/agent-center/message/send',
			  data: angular.toJson(message)
			}).then(function successCallback(response) {
				 self.handleErrorResponse(response.data)
			  }, function errorCallback(response) {
				  
			  });
	}
	this.handleErrorResponse = function(error) {
		if(error == 1) {
			toastr.error("Agent sender is no longer running.");
		}
		else if(error == 2) {
			toastr.error("One or more agent recievers are no longer running.");
		}
		else if(error == 3) {
			toastr.error("Agent to reply to is no longer running.");
		}
		else {
			toastr.info("Message successfuly sent.");
		}
	}
}]);

rsMessagingModule.service('rsAgentService', ['$http', function($http) { 
	this.startAgent = function(agent, runningAgents) {
		if(agent.name == null || agent.name == "") {
			toastr.warning("Agent name must be choosen.");
			return;
		}
		
		for(var i = 0; i < runningAgents.length; i++) {
			if(runningAgents[i].id.name == agent.name) {
				toastr.error("Agent with choosen name already exists.");
				agent.name = "";
				return;
			}
		}
		
		var self = this;
		return $http({
			  method: 'PUT',
			  url: '../agent-center-dc/rest/agent-center/agent/start/' + agent.type + "/" + agent.name
			}).then(function successCallback(response) {
				 self.handleErrorResponse(response.data);
				 agent.name = "";
				 return response;
			  }, function errorCallback(response) {
				  
			  });
	}
	
	this.stopAgent = function(runningAgents, agentName, index) {
		for(var i = 0;i < runningAgents.length; i++) {
			if(runningAgents[i].id.name == agentName) {
				runningAgents.splice(i, 1);
				break;
			}
		}
		$http({
			  method: 'DELETE',
			  url: '../agent-center-dc/rest/agent-center/agent/stop/' + agentName
			}).then(function successCallback(response) {
				
			  }, function errorCallback(response) {
				  
			  });
	}
	
	this.handleErrorResponse = function(error) {
		if(error == 4) {
			toastr.error("Agent with that name is already started.");
		}
		else if(error == 5) {
			toastr.error("Choosen agent type doesn't exist anymore.");
		}
		else if(error == 6) {
			toastr.error("Agent failed to start.");
		}
		else if(error == 0){
			toastr.info("Agent successfuly started.");
		}
	}
}]);