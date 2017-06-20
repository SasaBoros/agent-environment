package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.NodeData;
import entities.AgentCenter;
import entities.AgentType;
import utilities.Util;

@Stateless
public class NodeService {

	@Inject
	private NodeData nodeData;

	public List<AgentCenter> getNodes() {
		return nodeData.getNodes();
	}

	public void setNodes(List<AgentCenter> nodes) {
		nodeData.setNodes(nodes);
	}

	public void addNode(AgentCenter node) {
		nodeData.addNode(node);
	}

	public void updateSystemWithNewNodeData(AgentCenter node) {
		if (System.getProperty(Util.MASTER_NODE) != null) {
			return;
		}
		try {
			List<AgentType> nodeAgentTypes = getNewNodeAgentTypes(node);

			nodeData.addNodeAgentTypes(node.getAddress(), nodeAgentTypes);

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
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agent-type/agent-types");

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

		for (AgentCenter n : nodeData.getNodes()) {
			if (n.getAddress().equals(node.getAddress())) {
				continue;
			}

			try {
				ResteasyWebTarget target = client
						.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/node/register");
				target.request().post(Entity.entity(node, MediaType.APPLICATION_JSON));

				target = client.target("http://" + n.getAddress()
						+ "/agent-center-dc/rest/agent-center/agent-type/node-agent-types/" + node.getAddress());
				target.request().post(Entity.entity(nodeAgentTypes, MediaType.APPLICATION_JSON));
			} catch (Exception e) {
				// One of nodes stopped working.
			}
		}
	}

	private void updateNewNode(AgentCenter node) {

		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/node/nodes");
		try {
			target.request().post(Entity.entity(nodeData.getNodes().stream().filter(n -> {
				return !n.getAddress().equals(node.getAddress());
			}).collect(Collectors.toList()), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(nodeData.getNodes().stream().filter(n -> {
				return !n.getAddress().equals(node.getAddress());
			}).collect(Collectors.toList()), MediaType.APPLICATION_JSON));
		}
		target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agent-type/agent-types");
		try {
			target.request().post(Entity.entity(nodeData.getNodeAgentTypes(), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(nodeData.getNodeAgentTypes(), MediaType.APPLICATION_JSON));
		}
		
		target = client
				.target("http://" + node.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agents");
		try {
			target.request().post(Entity.entity(nodeData.getRunningAgents(), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			e.printStackTrace();
			target.request().post(Entity.entity(nodeData.getRunningAgents(), MediaType.APPLICATION_JSON));
		}
	}

	private void rollbackSystemChanges(AgentCenter node) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		nodeData.removeNode(node);
		for (AgentCenter n : nodeData.getNodes()) {
			if (n.getAddress().equals(node.getAddress())) {
				continue;
			}
			ResteasyWebTarget target = client.target("http://" + n.getAddress()
					+ "/agent-center-dc/rest/agent-center/node/unregister/" + node.getAddress());
			target.request().delete();
		}

	}

	public void removeNode(String nodeAddress) {

		for (AgentCenter n : nodeData.getNodes()) {
			if (n.getAddress().equals(nodeAddress)) {
				nodeData.removeNode(n);
				break;
			}
		}

		nodeData.removeNodeAgentTypes(nodeAddress);
		
		nodeData.setRunningAgents(nodeData.getRunningAgents().stream()
				.filter(runningAgent -> !runningAgent.getId().getHost().getAddress().equals(nodeAddress))
				.collect(Collectors.toList()));
	}

}
