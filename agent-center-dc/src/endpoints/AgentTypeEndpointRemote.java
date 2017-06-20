package endpoints;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import entities.AgentType;

@Remote
public interface AgentTypeEndpointRemote {

	List<AgentType> getAgentTypes();
	
	void setNodeAgentTypes(Map<String, List<AgentType>> nodeAgentTypes);
	
	void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes);
	
}
