package endpoints;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entities.Message;
import entities.Performative;
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/send")
	@Override
	public Integer sendMessage(Message message) {
		return messageService.handleMessage(message);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/employ-agent/{agentName}")
	@Override
	public void employAgent(@PathParam("agentName") String agentName, Message message) {
		messageService.employAgent(message, agentName);
	}
	
}
