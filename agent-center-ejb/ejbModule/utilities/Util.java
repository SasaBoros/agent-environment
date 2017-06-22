package utilities;

import java.util.List;

import entities.AID;
import entities.Agent;
import entities.AgentType;

public final class Util {
	
	public static final String MASTER_NODE = "master-node";
	public static final String THIS_NODE = "this-node";
	public static final String ALIAS = "alias";
	public static final String AGENT_TYPES_PATH = "agent-types-path";
	
	public static Boolean isAgentTypeAvailable(String type, List<AgentType> types) {
		for (AgentType at : types) {
			if (at.getName().equals(type)) {
				return true;
			}
		}
		return false;
	}
	
	public static Agent getAgentById(AID id, List<Agent> agents) {
		for(Agent agent : agents) {
			if(agent.getId().getName().equals(id.getName())) {
				return agent;
			}
		}
		return null;
	}
}
