package endpoints;

import javax.ejb.Remote;

import entities.Message;
import entities.Performative;

@Remote
public interface MessageEndpointRemote {

	Performative[] getPerformatives();

	Integer sendMessage(Message message);

	void employAgent(String agentName, Message message);
}
