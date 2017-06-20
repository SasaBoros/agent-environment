package endpoints;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import entities.Agent;
import service.AgentService;

@Stateless
@Path("/agent")
public class AgentEndpoint implements AgentEndpointRemote {
	
	@Inject
	private AgentService agentService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/running-agents")
	@Override
	public List<Agent> getRunningAgents() {
		return agentService.getRunningAgents();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/running-agents")
	@Override
	public void setRunningAgents(List<Agent> runningAgents) {
		agentService.setRunningAgents(runningAgents);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/running-agent")
	@Override
	public void addRunningAgent(Agent runningAgent) {
		agentService.addRunningAgent(runningAgent);
	}
	
	@DELETE
	@Path("/running-agent/{agentName}")
	@Override
	public void removeRunningAgent(@PathParam("agentName") String agentName) {
		agentService.removeRunningAgent(agentName);
	}

	@PUT
	@Path("/start/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Integer startAgent(@PathParam("type") String type, @PathParam("name") String name, @Context HttpServletRequest request) {
		System.out.println(request.getRemoteHost() + ":" + request.getRemotePort());
		return agentService.startAgent(type, name, request.getRemoteHost() + ":" + request.getRemotePort());
	}

	@DELETE
	@Path("/stop/{name}")
	@Override
	public void stopAgent(@PathParam("name") String name) {
		 agentService.stopAgent(name);
	}
}
