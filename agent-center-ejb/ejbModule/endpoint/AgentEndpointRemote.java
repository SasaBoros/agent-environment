package endpoint;

import java.util.List;

import javax.ejb.Remote;
import javax.ws.rs.core.Response;

import model.Agent;

@Remote
public interface AgentEndpointRemote {

	List<Agent> getRunningAgents();

	Response startAgent(String type, String name, Boolean isSlave);
	
	Agent delegateStartAgent(String type, String name);

	void stopAgent(String aid);
	
	void setRunningAgents(List<Agent> runningAgents);

	void addRunningAgent(Agent runningAgent);

	void removeRunningAgent(String agentName);
}
