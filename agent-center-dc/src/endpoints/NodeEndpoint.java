package endpoints;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entities.AgentCenter;
import service.NodeService;

@Stateless
@Path("/node")
public class NodeEndpoint implements NodeEndpointRemote {
	
	@Inject
	private NodeService nodeService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node")
	@Override
	public void getNodes() {
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	@Override
	public void setNodes(List<AgentCenter> nodes) {
		nodeService.setNodes(nodes);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/register")
	@Override
	public void registerNode(AgentCenter node) {
		nodeService.addNode(node);
		nodeService.updateSystemWithNewNodeData(node);
	}

	

	@DELETE
	@Path("/unregister/{nodeAddress}")
	@Override
	public void unregisterNode(@PathParam("nodeAddress") String nodeAddress) {
		nodeService.removeNode(nodeAddress);
	}
}
