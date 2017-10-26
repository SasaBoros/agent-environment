package service;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import data.NodeData;
import model.AgentType;

@Stateless
public class AgentTypeService {
	
	@Inject
	private NodeData nodeData;

	public void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodeData.addNodeAgentTypes(nodeAddress, agentTypes);
	}

	public void setNodeAgentTypes(Map<String, List<AgentType>> nodeAgentTypes) {
		nodeData.setNodeAgentTypes(nodeAgentTypes);
	}

	public List<AgentType> getAllAgentTypes() {
		return nodeData.getAllAgentTypes();
	}

}
