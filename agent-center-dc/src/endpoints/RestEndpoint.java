package endpoints;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import data.NodeData;
import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;
import messaging.ErrorResponse;
import service.RestEndpointService;
import service.Util;

@Stateless
@Path("/agent-center")
public class RestEndpoint implements RestEndpointRemote {
	
	@Inject
	private RestEndpointService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/types")
	@Override
	public List<AgentType> getAgentTypes() {
		return NodeData.getAllAgentTypes();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/agents/types/{nodeAddress}")
	@Override
	public void addNodeAgentTypes(@PathParam("nodeAddress") String nodeAddress, List<AgentType> agentTypes) {
		NodeData.addNodeAgentTypes(nodeAddress, agentTypes);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/agents/types")
	@Override
	public void addNodesAgentTypes(Map<String, List<AgentType>> nodeAgentTypes) {
		NodeData.setNodesAgentTypes(nodeAgentTypes);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/performatives")
	@Override
	public Performative[] getPerformatives() {
		return Performative.values();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agents")
	@Override
	public List<Agent> getRunningAgents() {
		return NodeData.getRunningAgents();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agents")
	@Override
	public void setRunningAgents(List<Agent> runningAgents) {
		NodeData.setRunningAgents(runningAgents);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/agents/running-agent")
	@Override
	public void addRunningAgent(Agent runningAgent) {
		NodeData.addRunningAgent(runningAgent);
	}

	@PUT
	@Path("/agents/agent/start/{type}/{name}")
	@Override
	public Integer startAgent(@PathParam("type") String type, @PathParam("name") String name) {
		return service.createAgent(type, name);
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
		service.updateSystemWithNewNodeData(node);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node/register-nodes")
	@Override
	public void registerNodes(List<AgentCenter> nodes) {
		NodeData.setNodes(nodes);
	}

	@DELETE
	@Path("/node/unregister/{nodeAddress}")
	@Override
	public void unregisterNode(@PathParam("nodeAddress") String nodeAddress) {
		NodeData.removeNode(nodeAddress);
	}

}
