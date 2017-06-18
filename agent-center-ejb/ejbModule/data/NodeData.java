package data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import entities.AgentCenter;
import utilities.Util;

@Startup
@Singleton
public class NodeData {

	private static List<AgentCenter> nodes = new ArrayList<AgentCenter>();

	@PostConstruct
	void init() {
		if(System.getProperty(Util.MASTER_NODE) != null) {
			ResteasyClient client = new ResteasyClientBuilder().build();

			ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE) + "/agent-center-dc/rest/agent-center/node/register");
			target.request().post(Entity.entity(new AgentCenter(System.getProperty(Util.ALIAS), System.getProperty(Util.THIS_NODE)), MediaType.APPLICATION_JSON));
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
	
	public static void removeNode(AgentCenter node) {
		for(AgentCenter n : nodes) {
			if(n.getAddress().equals(node.getAddress())) {
				nodes.remove(n);
				return;
			}
		}
	}
}
