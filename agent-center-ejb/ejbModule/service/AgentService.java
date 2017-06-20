package service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import messaging.ErrorResponse;
import utilities.Util;

@Stateless
public class AgentService {

	@Inject
	private NodeData nodeData;

	public List<Agent> getRunningAgents() {
		return nodeData.getRunningAgents();
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		nodeData.setRunningAgents(runningAgents);
	}

	public void addRunningAgent(Agent runningAgent) {
		nodeData.addRunningAgent(runningAgent);
	}

	public Integer startAgent(String type, String name) {

		for (Agent agent : nodeData.getRunningAgents()) {
			if (agent.getId().getName().equals(name)) {
				return ErrorResponse.AGENT_NAME_ALREADY_EXISTS;
			}
		}

		ResteasyClient client = new ResteasyClientBuilder().build();

		try {
			Agent agent = (Agent) Class.forName("agents." + type).newInstance();
			agent.setId(new AID(name, new AgentCenter(System.getProperty(Util.THIS_NODE)), new AgentType(type)));
			nodeData.addRunningAgent(agent);
			if (System.getProperty(Util.MASTER_NODE) != null) {
				ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
						+ "/agent-center-dc/rest/agent-center/agent/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}

			for (AgentCenter n : nodeData.getNodes()) {
				if (n.getAddress().equals(System.getProperty(Util.THIS_NODE))) {
					continue;
				}
				ResteasyWebTarget target = client
						.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}
		} catch (Exception e) {

		}

		return ErrorResponse.ERRORFREE;
	}

	public void stopAgent(String agentName) {
		for (Agent a : nodeData.getRunningAgents()) {
			if (a.getId().getName().equals(agentName)) {
				nodeData.removeRunningAgent(a);
				break;
			}
		}
		
		ResteasyClient client = new ResteasyClientBuilder().build();

		if (System.getProperty(Util.MASTER_NODE) != null) {
			ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
					+ "/agent-center-dc/rest/agent-center/agent/running-agent/" + agentName);
			target.request().delete();
		}

		for (AgentCenter n : nodeData.getNodes()) {
			if (n.getAddress().equals(System.getProperty(Util.THIS_NODE))) {
				continue;
			}
			ResteasyWebTarget target = client.target(
					"http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agent/" + agentName);
			target.request().delete();
		}

	}

	public void removeRunningAgent(String agentName) {
		for (Agent a : nodeData.getRunningAgents()) {
			if (a.getId().getName().equals(agentName)) {
				nodeData.removeRunningAgent(a);
				break;
			}
		}
	}

}
