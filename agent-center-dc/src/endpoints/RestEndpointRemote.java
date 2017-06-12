package endpoints;

import java.util.List;

import javax.ejb.Remote;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import data.Performative;

@Remote
public interface RestEndpointRemote {

	List<AgentType> getAgentTypes();

	List<Agent> getRunningAgents();

	void startAgent(String type, String name);

	void stopAgent(String aid);

	void sendMessage(Message message);

	Performative[] getPerformatives();

	List<AgentCenter> getNodes();

	void registerNode(AgentCenter node);

	void unregisterNode(AgentCenter node);

}
