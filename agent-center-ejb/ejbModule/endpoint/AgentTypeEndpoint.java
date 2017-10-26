package endpoint;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AgentType;
import service.AgentTypeService;

@Stateless
@Path("/agent-type")
public class AgentTypeEndpoint implements AgentTypeEndpointRemote {
	
	@Inject
	private AgentTypeService agentTypeService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agent-types")
	@Override
	public List<AgentType> getAgentTypes() {
		return agentTypeService.getAllAgentTypes();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/agent-types")
	@Override
	public void setNodeAgentTypes(Map<String, List<AgentType>> nodeAgentTypes) {
		agentTypeService.setNodeAgentTypes(nodeAgentTypes);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node-agent-types/{nodeAddress}")
	@Override
	public void addNodeAgentTypes(@PathParam("nodeAddress") String nodeAddress, List<AgentType> agentTypes) {
		agentTypeService.addNodeAgentTypes(nodeAddress, agentTypes);
	}

}
