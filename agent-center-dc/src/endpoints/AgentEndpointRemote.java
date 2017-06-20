package endpoints;

import java.util.List;

import javax.ejb.Remote;

import entities.Agent;

@Remote
public interface AgentEndpointRemote {

	List<Agent> getRunningAgents();

	Integer startAgent(String type, String name);

	void stopAgent(String aid);
	
	void setRunningAgents(List<Agent> runningAgents);

	void addRunningAgent(Agent runningAgent);

	void removeRunningAgent(String agentName);
}
