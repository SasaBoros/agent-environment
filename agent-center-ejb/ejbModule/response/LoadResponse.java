package response;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import data.AgentData;
import entities.Agent;
import entities.Performative;

public class LoadResponse implements Serializable {

	private static final long serialVersionUID = -4692388250156866326L;

	private List<Performative> performative = Arrays.asList(Performative.values());
	private List<Agent> agents = AgentData.getAgents();
	private List<Agent> runningAgents = AgentData.getRunningAgents();

	public LoadResponse() {
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	public List<Performative> getPerformative() {
		return performative;
	}

	public void setPerformative(List<Performative> performative) {
		this.performative = performative;
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}

}
