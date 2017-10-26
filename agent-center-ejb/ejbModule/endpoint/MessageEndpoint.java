package endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.ACLMessage;
import model.Performative;
import service.MessageService;

@Stateless
@Path("/message")
public class MessageEndpoint implements MessageEndpointRemote {

	@Inject
	private MessageService messageService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/performatives")
	@Override
	public Performative[] getPerformatives() {
		return messageService.getPerformatives();
	}

	@POST
	@Path("/send")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Response sendMessage(ACLMessage message) {
		return messageService.handleMessage(message);
	}

}
