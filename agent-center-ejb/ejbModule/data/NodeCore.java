package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import entities.AgentCenter;
import entities.AgentType;
import utilities.Util;

@Singleton
@Startup
@DependsOn("NodeData")
public class NodeCore {

	@Inject
	private NodeData nodeData;
	
	@PostConstruct
	void init() {
		loadAgentTypes();
		
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
	
	private void loadAgentTypes() {

		if (System.getProperty(Util.AGENT_TYPES_PATH) == null) {
			throw new RuntimeException("Agent-types path missing from runtime argument list.");
		}
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(System.getProperty(Util.AGENT_TYPES_PATH)));
			List<AgentType> agentTypes = new ArrayList<AgentType>();
			while ((line = reader.readLine()) != null) {
				String[] agentTypeNames = line.split(",");
				for (String agentTypeName : agentTypeNames) {
					Class.forName("agents." + agentTypeName);
					agentTypes.add(new AgentType(agentTypeName));
				}
			}
			nodeData.addNodeAgentTypes(System.getProperty(Util.THIS_NODE), agentTypes);
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
	
	@PreDestroy
	void cleanNodeData() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		if(System.getProperty(Util.MASTER_NODE) != null) {
			ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
			+ "/agent-center-dc/rest/agent-center/node/unregister/" + System.getProperty(Util.THIS_NODE));
			target.request().delete();
		}
		
		for (AgentCenter n : nodeData.getNodes()) {
			ResteasyWebTarget target = client.target("http://" + n.getAddress()
					+ "/agent-center-dc/rest/agent-center/node/unregister/" + System.getProperty(Util.THIS_NODE));
			target.request().delete();
		}
	}
	
	
}
