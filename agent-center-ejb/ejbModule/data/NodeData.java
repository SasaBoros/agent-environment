package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import service.Util;

@Startup
@Singleton
public class NodeData {

	private static List<AgentCenter> nodes = new ArrayList<AgentCenter>();

	private static Map<String, List<AgentType>> nodesAgentTypes = new HashMap<String, List<AgentType>>();
	private static List<Agent> runningAgents = new ArrayList<Agent>();

	@PostConstruct
	void init() {
		loadAgentTypes();

		nodesAgentTypes.remove(null);

		if (System.getProperty(Util.MASTER_NODE) != null) {

			ResteasyClient client = new ResteasyClientBuilder().build();

			ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
					+ "/agent-center-dc/rest/agent-center/node/register");
			target.request()
					.post(Entity.entity(
							new AgentCenter(System.getProperty(Util.ALIAS), System.getProperty(Util.THIS_NODE)),
							MediaType.APPLICATION_JSON));
		}
	}
	
	@PreDestroy
	public void removeNodeFromSystem() {
		if(System.getProperty(Util.MASTER_NODE) == null) {
			return;
		}
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
		+ "/agent-center-dc/rest/agent-center/node/unregister/" + System.getProperty(Util.THIS_NODE));
		target.request().delete();

		for (AgentCenter n : NodeData.getNodes()) {
			if (n.getAddress().equals(System.getProperty(Util.THIS_NODE))) {
				continue;
			}
			target = client.target("http://" + n.getAddress()
					+ "/agent-center-dc/rest/agent-center/node/unregister/" + System.getProperty(Util.THIS_NODE));
			target.request().delete();
		}
	}

	private void loadAgentTypes() {

		if (System.getProperty(Util.AGENT_TYPES_PATH) == null) {
			throw new RuntimeException("Agent-types path missing from runtime argument list.");
		}
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(System.getProperty(Util.AGENT_TYPES_PATH)));
			nodesAgentTypes.put(System.getProperty(Util.THIS_NODE), new ArrayList<AgentType>());
			while ((line = reader.readLine()) != null) {
				String[] agentTypeNames = line.split(",");
				for (String agentTypeName : agentTypeNames) {
					Class.forName("agents." + agentTypeName);
					nodesAgentTypes.get(System.getProperty(Util.THIS_NODE)).add(new AgentType(agentTypeName));
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Invalid absolute path of agent-types file.");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Invalid agent type name in agent-type file.");
		} catch (Exception e) {
			throw new RuntimeException("Invalid agent-type file format.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static List<AgentCenter> getNodes() {
		return nodes;
	}

	public static void setNodes(List<AgentCenter> nodes) {
		NodeData.nodes = nodes;
	}

	public static void addNode(AgentCenter node) {
		nodes.add(node);
	}

	public static void removeNode(String nodeAddress) {
		for (AgentCenter n : nodes) {
			if (n.getAddress().equals(nodeAddress)) {
				nodes.remove(n);
				break;
			}
		}

		nodesAgentTypes.remove(nodeAddress);

		for (Agent agent : runningAgents) {
			if (agent.getId().getHost().getAddress().equals(nodeAddress)) {
				runningAgents.remove(agent);
				return;
			}
		}
	}

	public static List<AgentType> getAllAgentTypes() {
		List<AgentType> agentTypes = new ArrayList<AgentType>();
		for (Map.Entry<String, List<AgentType>> entry : nodesAgentTypes.entrySet()) {

			agentTypes.addAll(entry.getValue());

		}
		return agentTypes;
	}

	public static Map<String, List<AgentType>> getNodesAgentTypes() {
		return nodesAgentTypes;
	}

	public static void setNodesAgentTypes(Map<String, List<AgentType>> nodesAgentTypes) {
		NodeData.nodesAgentTypes = nodesAgentTypes;
	}

	public static void addNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodesAgentTypes.put(nodeAddress, agentTypes);
	}

	public static List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public static void setRunningAgents(List<Agent> runningAgents) {
		NodeData.runningAgents = runningAgents;
	}

	public static void removeRunningAgent(Agent agent) {
		for (Agent a : runningAgents) {
			if (a.getId().getName().equals(agent.getId().getName())) {
				runningAgents.remove(a);
				return;
			}
		}

	}

	public static void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
	}

}
