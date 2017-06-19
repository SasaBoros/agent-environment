package endpoints;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;

@Remote
public interface RestEndpointRemote {

	List<AgentType> getAgentTypes();
	
	void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes);

	Performative[] getPerformatives();
	
	List<Agent> getRunningAgents();

	Integer startAgent(String type, String name);

	Integer stopAgent(String aid);

	Integer sendMessage(Message message);

	List<AgentCenter> getNodes();

	void registerNode(AgentCenter node);

	void unregisterNode(String nodeAddress);

	void addNodesAgentTypes(Map<String, List<AgentType>> nodeAgentTypes);

	void registerNodes(List<AgentCenter> nodes);

	void setRunningAgents(List<Agent> runningAgents);

	void addRunningAgent(Agent runningAgent);

}
