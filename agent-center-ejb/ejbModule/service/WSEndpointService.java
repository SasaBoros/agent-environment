package service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
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
			sendWSMessage(clientSession, WSMessageType.AGENT_TYPES,
					mapper.writeValueAsString(nodeData.getAllAgentTypes()));
			sendWSMessage(clientSession, WSMessageType.RUNNING_AGENTS,
					mapper.writeValueAsString(nodeData.getRunningAgents()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addClientSession(Session clientSession) {
		clientData.addClientSession(clientSession);
	}

	public void removeClientSession(Session clientSession) {
		for (Session cs : clientData.getClientSessions()) {
			if (cs.getId().equals(clientSession.getId())) {
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

		if (wsMessage == null) {
			System.out.println("AAA");
			return;
		}

		if (wsMessage.getMessageType().equals(WSMessageType.START_AGENT)) {
			startAgent(clientSession, wsMessage.getContent());
		} else if (wsMessage.getMessageType().equals(WSMessageType.STOP_AGENT)) {
			stopAgent(clientSession, wsMessage.getContent());
		} else if (wsMessage.getMessageType().equals(WSMessageType.MESSAGE)) {

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

		ObjectMapper mapper = new ObjectMapper();
		ResteasyClient client = new ResteasyClientBuilder().build();

		for (Agent agent : nodeData.getRunningAgents()) {
			if (agent.getId().getName().equals(name)) {
				try {
					sendWSMessage(clientSession, WSMessageType.ERROR,
							mapper.writeValueAsString(ErrorResponse.AGENT_NAME_ALREADY_EXISTS_ERROR_TEXT));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return;
			}
		}

		if (!isTypeAvailable(type, nodeData.getThisNodeAgentTypes())) {
			for (Map.Entry<String, List<AgentType>> entry : nodeData.getNodeAgentTypes().entrySet()) {
				if (!entry.getKey().equals(System.getProperty(Util.THIS_NODE))
						&& isTypeAvailable(type, entry.getValue())) {
					ResteasyWebTarget target = client.target("http://" + entry.getKey()
							+ "/agent-center-dc/rest/agent-center/agent/delegate-start/" + type + "/" + name);
					try {
						Agent agent = target.request().put(null).readEntity(Agent.class);
						notifyAll(agent);
						sendWSMessage(clientSession, WSMessageType.ERROR_FREE,
								mapper.writeValueAsString(ErrorResponse.AGENT_SUCCESFULLY_STARTED_TEXT));
						return;
					} catch (Exception e) {
						try {
							sendWSMessage(clientSession, WSMessageType.ERROR,
									mapper.writeValueAsString(ErrorResponse.AGENT_FAILED_TO_START_TEXT));
						} catch (JsonProcessingException e1) {
							e1.printStackTrace();
						}
						return;
					}
				}
			}

			try {
				sendWSMessage(clientSession, WSMessageType.ERROR,
						mapper.writeValueAsString(ErrorResponse.AGENT_TYPE_DOESNT_EXIST));
				return;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		try {
			Agent agent = (Agent) Class.forName("agents." + type).newInstance();
			agent.setId(new AID(name, new AgentCenter(System.getProperty(Util.THIS_NODE)), new AgentType(type)));
			notifyAll(agent);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			try {
				sendWSMessage(clientSession, WSMessageType.ERROR,
						mapper.writeValueAsString(ErrorResponse.AGENT_TYPE_DOESNT_EXIST));
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void notifyAll(Agent agent) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		try {
			nodeData.addRunningAgent(agent);

			if (System.getProperty(Util.MASTER_NODE) != null) {
				ResteasyWebTarget target = client.target("http://" + System.getProperty(Util.MASTER_NODE)
						+ "/agent-center-dc/rest/agent-center/agent/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}

			for (AgentCenter n : nodeData.getNodes()) {
				ResteasyWebTarget target = client
						.target("http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agent");
				target.request().post(Entity.entity(agent, MediaType.APPLICATION_JSON));
			}

		} catch (Exception e) {

		}
	}

	private boolean isTypeAvailable(String type, List<AgentType> types) {
		for (AgentType at : types) {
			if (at.getName().equals(type)) {
				return true;
			}
		}
		return false;
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
			ResteasyWebTarget target = client.target(
					"http://" + n.getAddress() + "/agent-center-dc/rest/agent-center/agent/running-agent/" + agentName);
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
