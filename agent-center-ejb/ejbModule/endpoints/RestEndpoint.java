package endpoints;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;

@Stateless
@Path("/agent-center")
public class RestEndpoint implements RestEndpointRemote {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/types")
	public List<AgentType> getAgentTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agents")
	public List<Agent> getRunningAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@PUT
	@Path("/agents/agent/start/{type}/{name}")
	public void startAgent() {
		// TODO Auto-generated method stub

	}

	@DELETE
	@Path("/agents/agent/stop/{aid}")
	public void stopAgent() {
		// TODO Auto-generated method stub

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/message")
	public void sendMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/performatives")
	public List<Performative> getPerformatives() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	public List<AgentCenter> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/register")
	public void registerNode(AgentCenter node) {
		
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/unregister")
	public void unregisterNode(AgentCenter node) {
		
	}

}
