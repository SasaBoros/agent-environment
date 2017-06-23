package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import messaging.WSMessage;
import messaging.WSMessageType;
import utilities.Util;

@Singleton
public class NodeData {

	private List<AgentCenter> nodes = new ArrayList<AgentCenter>();

	private Map<String, List<AgentType>> nodesAgentTypes = new HashMap<String, List<AgentType>>();
	private List<Agent> runningAgents = new ArrayList<Agent>();
	
	@Inject
	private ClientData clientData;

	private ObjectMapper mapper = new ObjectMapper();

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
	
	public List<AgentType> getThisNodeAgentTypes() {
		return nodesAgentTypes.get(System.getProperty(Util.THIS_NODE));
	}

	public void setNodeAgentTypes(Map<String, List<AgentType>> nodesAgentTypes) {
		this.nodesAgentTypes = nodesAgentTypes;
		sendChangeToWSClient(WSMessageType.AGENT_TYPES, null);
	}

	public void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodesAgentTypes.put(nodeAddress, agentTypes);
		sendChangeToWSClient(WSMessageType.AGENT_TYPES, null);
	}

	public void removeNodeAgentTypes(String nodeAddress) {
		nodesAgentTypes.remove(nodeAddress);
		sendChangeToWSClient(WSMessageType.AGENT_TYPES, null);
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
		sendChangeToWSClient(WSMessageType.RUNNING_AGENTS, null);
	}

	public void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
		sendChangeToWSClient(WSMessageType.STARTED_AGENT, agent);
	}

	public void removeRunningAgent(Agent agent) {
		runningAgents.remove(agent);
		sendChangeToWSClient(WSMessageType.DELETED_AGENT, agent);
	}

	public List<AgentType> getAllAgentTypes() {
		List<AgentType> agentTypes = new ArrayList<AgentType>();
		for (Map.Entry<String, List<AgentType>> entry : nodesAgentTypes.entrySet()) {
			for(AgentType type : entry.getValue()) {
				if(!agentTypes.contains(type))
					agentTypes.add(type);
			}
		}
		
		return agentTypes;
	}
	

	@SuppressWarnings("incomplete-switch")
	private void sendChangeToWSClient(WSMessageType type, Agent agent) {
		
		for (Session cs : clientData.getClientSessions()) {
			try {
				switch (type) {
				case RUNNING_AGENTS:
					cs.getBasicRemote().sendText(
							mapper.writeValueAsString(new WSMessage(type, mapper.writeValueAsString(runningAgents))));
					break;
				case AGENT_TYPES:
					cs.getBasicRemote().sendText(mapper
							.writeValueAsString(new WSMessage(type, mapper.writeValueAsString(getAllAgentTypes()))));
					break;
				case STARTED_AGENT:
					cs.getBasicRemote()
							.sendText(mapper.writeValueAsString(new WSMessage(type, mapper.writeValueAsString(agent))));
					break;
				case DELETED_AGENT:
					cs.getBasicRemote().sendText(mapper.writeValueAsString(
							new WSMessage(type, mapper.writeValueAsString(agent.getId().getName()))));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
