package data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;

import entities.Agent;

@Singleton
public class AgentData {

	private static List<Agent> agents = new ArrayList<Agent>();
	private static List<Agent> runningAgents = new ArrayList<Agent>();

	public AgentData() {
		// TODO : Create all available agents
	}

	public static List<Agent> getAgents() {
		return agents;
	}

	public static void setAgents(List<Agent> agents) {
		AgentData.agents = agents;
	}

	public static List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public static void setRunningAgents(List<Agent> runningAgents) {
		AgentData.runningAgents = runningAgents;
	}

}
