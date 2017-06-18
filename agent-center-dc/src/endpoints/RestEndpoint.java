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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

import agents.MapReduce;
import agents.Ping;
import agents.Pong;
import data.AgentData;
import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;
import messaging.ErrorResponse;
import utilities.Util;



@Stateless
@Path("/agent-center")
public class RestEndpoint implements RestEndpointRemote {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/types")
	@Override
	public List<AgentType> getAgentTypes() {
		
		return AgentData.getNodeAgentTypes().get(System.getProperty(Util.THIS_NODE));
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/performatives")
	@Override
	public Performative[] getPerformatives() {
		// TODO Auto-generated method stub
		System.out.println("Get runningAgents");
		return Performative.values();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agents")
	@Override
	public List<Agent> getRunningAgents() {
		
		return AgentData.getNodeRunningAgents().get(System.getProperty(Util.THIS_NODE));
	}

	@PUT
	@Path("/agents/agent/start/{type}/{name}")
	@Override
	public Integer startAgent(@PathParam("type")String type, @PathParam("name")String name) {
		System.out.println(type + " " + name);
		return ErrorResponse.ERRORFREE;
	}

	@DELETE
	@Path("/agents/agent/stop/{aid}")
	@Override
	public Integer stopAgent(@PathParam("aid") String aid) {
		System.out.println(aid);
		return ErrorResponse.ERRORFREE;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/message")
	@Override
	public Integer sendMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println(message);
		return ErrorResponse.ERRORFREE;
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
		NodeData.addNode(node);
		if(System.getProperty(Util.MASTER_NODE) == null) {
			ResteasyClient client = new ResteasyClientBuilder().build();

			ResteasyWebTarget target = client.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agents/types");
			List<AgentType> nodeAgentTypes = target.request().get(new GenericType<ArrayList<AgentType>>() {});
			AgentData.putNodeAgentTypes(node.getAddress(), nodeAgentTypes);
			
			for(AgentCenter n : NodeData.getNodes()) {
				if(n.getAddress().equals(node.getAddress()))
					continue;
				
				client = new ResteasyClientBuilder().build();
				target = client.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/node/register");
				target.request().post(Entity.entity(node, MediaType.APPLICATION_JSON));
			}
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/unregister")
	@Override
	public void unregisterNode(AgentCenter node) {
		
	}

}
