package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import messaging.WSMessageType;

@Singleton
public class NodeData {
	
	@Inject
	private ClientData clientData;

	private List<AgentCenter> nodes = new ArrayList<AgentCenter>();

	private Map<String, List<AgentType>> nodesAgentTypes = new HashMap<String, List<AgentType>>();
	private List<Agent> runningAgents = new ArrayList<Agent>();

	public List<AgentCenter> getNodes() {
		return nodes;
	}

	public void setNodes(List<AgentCenter> nodes) {
		this.nodes = nodes;
	}

	public void addNode(AgentCenter node) {
		nodes.add(node);
	}

	public void removeNode(AgentCenter node) {
		nodes.remove(node);
	}

	public Map<String, List<AgentType>> getNodeAgentTypes() {
		return nodesAgentTypes;
	}

	public void setNodeAgentTypes(Map<String, List<AgentType>> nodesAgentTypes) {
		this.nodesAgentTypes = nodesAgentTypes;
	}

	public void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodesAgentTypes.put(nodeAddress, agentTypes);
	}

	public void removeNodeAgentTypes(String nodeAddress) {
		nodesAgentTypes.remove(nodeAddress);
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
	}

	public void removeRunningAgent(Agent agent) {
		runningAgents.remove(agent);
	}

	public List<AgentType> getAllAgentTypes() {
		Set<AgentType> agentTypes = new HashSet<AgentType>();
		for (Map.Entry<String, List<AgentType>> entry : nodesAgentTypes.entrySet()) {
			agentTypes.addAll(entry.getValue());
		}
		return new ArrayList<AgentType>(agentTypes);
	}
	
	private void sendChangeToWSClient(WSMessageType type, String content) {
		
	}

}
