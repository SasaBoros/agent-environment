package data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import agents.MapReduce;
import agents.Ping;
import agents.Pong;
import entities.AID;
import entities.Agent;
import entities.AgentType;

@Startup
@Singleton
public class AgentData {

	private static List<AgentType> agentTypes = new ArrayList<AgentType>();
	private static List<Agent> runningAgents = new ArrayList<Agent>();
	
	@PostConstruct
	void init() {
		// TODO : dummie data
		agentTypes.add(new AgentType("Ping", "Egg"));
		agentTypes.add(new AgentType("Pong", "Egg"));
		agentTypes.add(new AgentType("MapReduce", "Egg"));

		runningAgents.add(new Ping(new AID("ping1")));
		runningAgents.add(new Pong(new AID("pong1")));
		runningAgents.add(new MapReduce(new AID("mapReduce1")));
		runningAgents.add(new MapReduce(new AID("mapReduce2")));
	}

	public static List<AgentType> getAgentTypes() {
		return agentTypes;
	}

	public static void setAgentTypes(List<AgentType> agentTypes) {
		AgentData.agentTypes = agentTypes;
	}

	public static List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public static void setRunningAgents(List<Agent> runningAgents) {
		AgentData.runningAgents = runningAgents;
	}

}
