package service;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;

import data.ClientData;
import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.AgentCenter;
import entities.AgentType;
import entities.Message;
import entities.Performative;
import messaging.ErrorResponse;
import messaging.WSMessage;
import messaging.WSMessageType;
import utilities.Util;

@Stateless
public class WSEndpointService {
	
	@Inject
	private ClientData clientData;
	
	@Inject
	private NodeData nodeData;
	
	public void sentDataToClient(Session clientSession) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			sendWSMessage(clientSession, WSMessageType.PERFORMATIVES, mapper.writeValueAsString(Performative.values()));
			sendWSMessage(clientSession, WSMessageType.AGENT_TYPES, mapper.writeValueAsString(nodeData.getAllAgentTypes()));
			sendWSMessage(clientSession, WSMessageType.RUNNING_AGENTS, mapper.writeValueAsString(nodeData.getRunningAgents()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addClientSession(Session clientSession) {
		clientData.addClientSession(clientSession);
	}

	public void removeClientSession(Session clientSession) {
		for(Session cs : clientData.getClientSessions()) {
			if(cs.getId().equals(clientSession.getId())) {
				clientData.removeClientSession(cs);
				return;
			}
		}
	}

	public void handleMessage(Session clientSession, String message) {
		ObjectMapper mapper = new ObjectMapper();
		WSMessage wsMessage = null;
		try {
			wsMessage = mapper.readValue(message, WSMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(wsMessage == null)
			return;
		
		if(wsMessage.getMessageType().equals(WSMessageType.START_AGENT)) {
			
			startAgent(clientSession, wsMessage.getContent());
		}
		else if(wsMessage.getMessageType().equals(WSMessageType.STOP_AGENT)) {
			stopAgent(clientSession, wsMessage.getContent());
		}
		else if(wsMessage.getMessageType().equals(WSMessageType.MESSAGE)) {
			
			System.out.println(wsMessage.getContent());
			try {
				Message m = mapper.readValue(wsMessage.getContent(), Message.class);
				System.out.println(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void startAgent(Session clientSession, String content) {
		String type = content.split("/")[0];
		String name = content.split("/")[1];
		
		for (Agent agent : nodeData.getRunningAgents()) {
			if (agent.getId().getName().equals(name)) {
				sendWSMessage(clientSession, WSMessageType.ERROR, ErrorResponse.AGENT_NAME_ALREADY_EXISTS_ERROR_TEXT);
				return;
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
	}
	
	private void stopAgent(Session clientSession, String agentName) {
		for (Agent agent : nodeData.getRunningAgents()) {
			if (agent.getId().getName().equals(agentName)) {
				nodeData.removeRunningAgent(agent);
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
			ResteasyWebTarget target = client
					.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agent/" + agentName);
			target.request().delete();
		}
	}
	
	private void sendWSMessage(Session clientSession, WSMessageType type, String content) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			clientSession.getBasicRemote().sendText(mapper.writeValueAsString(new WSMessage(type, content)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
