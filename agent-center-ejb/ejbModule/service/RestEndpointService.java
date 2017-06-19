package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
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

@Stateless
public class RestEndpointService {

	public void updateSystemWithNewNodeData(AgentCenter node) {
		if (System.getProperty(Util.MASTER_NODE) != null) {
			return;
		}
		try {

			List<AgentType> nodeAgentTypes = getNewNodeAgentTypes(node);

			NodeData.addNodeAgentTypes(node.getAddress(), nodeAgentTypes);

			updateExistingNodes(node, nodeAgentTypes);
			updateNewNode(node);

		} catch (Exception e) {
			rollbackSystemChanges(node);
			System.err.println("STARTING ROLLBACK");
		}
	}

	private List<AgentType> getNewNodeAgentTypes(AgentCenter node) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agents/types");

		List<AgentType> nodeAgentTypes = null;
		try {
			nodeAgentTypes = target.request().get(new GenericType<ArrayList<AgentType>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			nodeAgentTypes = target.request().get(new GenericType<ArrayList<AgentType>>() {
			});
		}

		return nodeAgentTypes;
	}

	private void updateExistingNodes(AgentCenter node, List<AgentType> nodeAgentTypes) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		for (AgentCenter n : NodeData.getNodes()) {
			if (n.getAddress().equals(node.getAddress())) {
				continue;
			}

			try {
				ResteasyWebTarget target = client
						.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/node/register");
				target.request().post(Entity.entity(node, MediaType.APPLICATION_JSON));

				target = client.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agents/types/"
						+ node.getAddress());
				target.request().post(Entity.entity(nodeAgentTypes, MediaType.APPLICATION_JSON));
			} catch (Exception e) {
				// One of nodes stopped working.
			}
		}
	}

	private void updateNewNode(AgentCenter node) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/node/register-nodes");
		try {
			target.request().post(Entity.entity(NodeData.getNodes().stream().filter(n -> {
				return !n.getAddress().equals(node.getAddress());
			}).collect(Collectors.toList()), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(NodeData.getNodes().stream().filter(n -> {
				return !n.getAddress().equals(node.getAddress());
			}).collect(Collectors.toList()), MediaType.APPLICATION_JSON));
		}

		target = client.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agents/types");
		try {
			target.request().post(Entity.entity(NodeData.getNodesAgentTypes(), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(NodeData.getNodesAgentTypes(), MediaType.APPLICATION_JSON));
		}

		target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agents/running-agents");
		try {
			target.request().post(Entity.entity(NodeData.getRunningAgents(), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(NodeData.getRunningAgents(), MediaType.APPLICATION_JSON));
		}
	}

	private void rollbackSystemChanges(AgentCenter node) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		NodeData.removeNode(node.getAddress());
		for (AgentCenter n : NodeData.getNodes()) {
			if (n.getAddress().equals(node.getAddress())) {
				continue;
			}
			ResteasyWebTarget target = client.target("http://" + n.getAddress()
					+ "/agent-center-dc/rest/agent-center/node/unregister/" + node.getAddress());
			target.request().delete();
		}

	}

	public Integer createAgent(String type, String name) {
		for(Agent agent : NodeData.getRunningAgents())
		{
			if(agent.getId().getName().equals(name)) {
				return ErrorResponse.AGENT_NAME_ALREADY_EXISTS;
			}
		}
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		try {
			Agent agent = (Agent) Class.forName("agents." + type).newInstance();
			agent.setId(new AID(name, new AgentCenter(System.getProperty(Util.THIS_NODE)), new AgentType(type)));
			NodeData.addRunningAgent(agent);
			if(System.getProperty(Util.MASTER_NODE) != null) {
				ResteasyWebTarget target = client
						.target("http://" + System.getProperty(Util.MASTER_NODE) + "/agent-center-dc/rest/agent-center/agents/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}
			
			for (AgentCenter n : NodeData.getNodes()) {
				if (n.getAddress().equals(System.getProperty(Util.THIS_NODE))) {
					continue;
				}
				ResteasyWebTarget target = client
						.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agents/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}
		}
		catch(Exception e) {
		}
		
		return ErrorResponse.ERRORFREE;
	}
}
