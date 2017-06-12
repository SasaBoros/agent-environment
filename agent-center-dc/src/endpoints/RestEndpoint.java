package endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agents.MapReduce;
import agents.Ping;
import agents.Pong;
import entities.AID;
import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import data.Performative;



@Stateless
@Path("/agent-center")
public class RestEndpoint implements RestEndpointRemote {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/types")
	@Override
	public List<AgentType> getAgentTypes() {
		List<AgentType> agentTypes = new ArrayList<AgentType>();
		agentTypes.add(new AgentType("Ping", "Egg"));
		agentTypes.add(new AgentType("Pong", "Egg"));
		agentTypes.add(new AgentType("MapReduce", "Egg"));
		
		return agentTypes;
		
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agents")
	@Override
	public List<Agent> getRunningAgents() {
		List<Agent> agents = new ArrayList<Agent>();
		agents.add(new Ping(new AID("ping1")));
		agents.add(new Pong(new AID("pong1")));
		agents.add(new MapReduce(new AID("mapReduce1")));
		agents.add(new MapReduce(new AID("mapReduce2")));
		return agents;
	}

	@PUT
	@Path("/agents/agent/start/{type}/{name}")
	@Override
	public void startAgent(@PathParam("type")String type, @PathParam("name")String name) {
		System.out.println(type + " " + name);

	}

	@DELETE
	@Path("/agents/agent/stop/{aid}")
	@Override
	public void stopAgent(@PathParam("aid") String aid) {
		System.out.println(aid);

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/message")
	@Override
	public void sendMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/performatives")
	@Override
	public Performative[] getPerformatives() {
		// TODO Auto-generated method stub
		return Performative.values();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	@Override
	public List<AgentCenter> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/register")
	@Override
	public void registerNode(AgentCenter node) {
		
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/unregister")
	@Override
	public void unregisterNode(AgentCenter node) {
		
	}

}
