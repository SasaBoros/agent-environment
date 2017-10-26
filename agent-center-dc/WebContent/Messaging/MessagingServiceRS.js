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
				toastr.info("Message successfuly sent.");
			  }, function errorCallback(response) {
				  toastr.error(response.data);
			  });
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
			  url: '../agent-center-dc/rest/agent-center/agent/start/' + agent.type + "/" + agent.name + "/false" 
			}).then(function successCallback(response) {
				toastr.info("Agent successfuly started.");
				 agent.name = "";
			  }, function errorCallback(response) {
				  toastr.error(response.data);
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
	
}]);