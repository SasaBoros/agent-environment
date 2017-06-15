package endpoints;

import java.util.List;

import javax.ejb.Remote;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;

@Remote
public interface RestEndpointRemote {

	List<AgentType> getAgentTypes();

	Performative[] getPerformatives();
	
	List<Agent> getRunningAgents();

	Integer startAgent(String type, String name);

	Integer stopAgent(String aid);

	Integer sendMessage(Message message);


	List<AgentCenter> getNodes();

	void registerNode(AgentCenter node);

	void unregisterNode(AgentCenter node);

}
