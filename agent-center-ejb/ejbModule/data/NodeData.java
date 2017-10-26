package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.websocket.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.WSMessage;
import model.WSMessageType;
import utility.Util;

@Singleton
@Startup
@DependsOn("WSClientData")
public class NodeData implements Serializable {

	private static final long serialVersionUID = 2514301890876260517L;

	private List<AgentCenter> nodes = new ArrayList<AgentCenter>();
	private Map<String, List<AgentType>> nodesAgentTypes = new HashMap<String, List<AgentType>>();
	private List<Agent> runningAgents = new ArrayList<Agent>();
	private List<Agent> slaveAgents = new ArrayList<Agent>();

	@Inject
	private WSClientData clientData;

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
		updateWSCLients(WSMessageType.AGENT_TYPES, null);
	}

	public void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodesAgentTypes.put(nodeAddress, agentTypes);
		updateWSCLients(WSMessageType.AGENT_TYPES, null);
	}


	public void removeNodeAgentTypes(String nodeAddress) {
		nodesAgentTypes.remove(nodeAddress);
		updateWSCLients(WSMessageType.AGENT_TYPES, null);
	}

	public void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
		updateWSCLients(WSMessageType.STARTED_AGENT, agent);
	}

	public void removeRunningAgent(Agent agent) {
		runningAgents.remove(agent);
		updateWSCLients(WSMessageType.DELETED_AGENT, agent);
	}
	
	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
		updateWSCLients(WSMessageType.RUNNING_AGENTS, null);
	}
	
	public List<Agent> getRunningAgents() {
		return runningAgents;
	}


	public List<AgentType> getAllAgentTypes() {
		List<AgentType> agentTypes = new ArrayList<AgentType>();
		for (Map.Entry<String, List<AgentType>> entry : nodesAgentTypes.entrySet()) {
			for (AgentType type : entry.getValue()) {
				if (!agentTypes.contains(type))
					agentTypes.add(type);
			}
		}

		return agentTypes;
	}

	public Agent findAgentByName(String name) {
		for (Agent agent : runningAgents) {
			if (agent.getId().getName().equals(name))
				return agent;
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private void updateWSCLients(WSMessageType type, Agent agent) {
		
		ObjectMapper mapper = new ObjectMapper();

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

	public WSClientData getClientData() {
		return clientData;
	}

	public List<Agent> getSlaveAgents() {
		return slaveAgents;
	}
	
	public void addSlaveAgent(Agent agent) {
		slaveAgents.add(agent);
	}
	
	public void removeSlaveAgent(Agent agent) {
		slaveAgents.remove(agent);
	}

}
