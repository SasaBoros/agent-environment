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
import javax.ejb.Singleton;
import javax.ejb.Startup;

import entities.Agent;
import entities.AgentType;
import utilities.Util;

@Startup
@Singleton
public class AgentData {

	private static Map<String, List<AgentType>> nodeAgentTypes = new HashMap<String, List<AgentType>>();
	private static Map<String, List<Agent>> nodeRunningAgents = new HashMap<String, List<Agent>>();

	@PostConstruct
	void init() {
		loadAgentTypes();
	}

	private void loadAgentTypes() {

		if (System.getProperty(Util.AGENT_TYPES_PATH) == null) {
			throw new RuntimeException("Agent-types path missing from runtime argument list.");
		}
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(System.getProperty(Util.AGENT_TYPES_PATH)));
			nodeAgentTypes.put(System.getProperty(Util.THIS_NODE), new ArrayList<AgentType>());
			while ((line = reader.readLine()) != null) {
				String[] agentTypeNames = line.split(",");
				for (String agentTypeName : agentTypeNames) {
					Class.forName("agents." + agentTypeName);
					nodeAgentTypes.get(System.getProperty(Util.THIS_NODE)).add(new AgentType(agentTypeName));
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

	public static Map<String, List<AgentType>> getNodeAgentTypes() {
		return nodeAgentTypes;
	}

	public static Map<String, List<Agent>> getNodeRunningAgents() {
		return nodeRunningAgents;
	}
	
	public static void putNodeAgentTypes(String nodeAddress, List<AgentType> agentTypes) {
		nodeAgentTypes.put(nodeAddress, agentTypes);
	}

	public static void putNodeRunningAgents(String nodeAddress, List<Agent> runningAgents) {
		nodeRunningAgents.put(nodeAddress, runningAgents);
	}

}
