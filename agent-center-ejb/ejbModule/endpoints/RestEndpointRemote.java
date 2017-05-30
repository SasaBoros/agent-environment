package endpoints;

import java.util.List;

import javax.ejb.Remote;

import entities.Agent;
import entities.AgentType;
import entities.Message;
import entities.Performative;

@Remote
public interface RestEndpointRemote {

	public List<AgentType> getAgentTypes();

	public List<Agent> getRunningAgents();

	public void startAgent();

	public void stopAgent();

	public void sendMessage(Message message);

	public List<Performative> getPerformatives();

}
