package endpoint;

import javax.ejb.Remote;
import javax.ws.rs.core.Response;

import model.ACLMessage;
import model.Performative;

@Remote
public interface MessageEndpointRemote {

	Performative[] getPerformatives();

	Response sendMessage(ACLMessage message);

}
