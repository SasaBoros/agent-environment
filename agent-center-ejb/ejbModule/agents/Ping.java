package agents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.Message;
import utilities.Util;

public class Ping extends Agent {

	private static final long serialVersionUID = -3168432547521573115L;

	private Map<String, Integer> pingPongInteraction = new HashMap<String, Integer>();

	@Inject
	private NodeData nodeData;

	public Ping() {
	}

	@JsonCreator
	public Ping(@JsonProperty("id") AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {
		System.out.println("Ping agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);

		if (message.getSender() == null) {
			return;
		}

		if (message.getSender().getType().getName().equals("Pong")) {
			if (pingPongInteraction.containsKey(message.getSender().getName())) {
				pingPongInteraction.put(message.getSender().getName(),
						pingPongInteraction.get(message.getSender().getName()) + 1);
			} else {
				pingPongInteraction.put(message.getSender().getName(), 1);
			}

			System.out.println("Ping(" + id.getName() + ")-Pong(" + message.getSender().getName() + ") count: "
					+ pingPongInteraction.get(message.getSender().getName()));
		}

		replyTo(message);

	}

	private void replyTo(Message message) {
		if (message.getReplyTo() == null || message.getReplyTo().getName().equals(id.getName()))
			return;

		if (message.getReplyTo().getHost().equals(System.getProperty(Util.THIS_NODE))) {
			for (Agent agent : nodeData.getRunningAgents()) {
				if (agent.getId().getName().equals(message.getReplyTo().getName())) {
					agent.handleMessage(message);
					break;
				}
			}
		} else {
			Message replyToMessage = new Message();
			replyToMessage.setSender(id);
			AID[] receiver = { message.getReplyTo() };
			replyToMessage.setReceivers(Arrays.asList(receiver));

			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://" + message.getReplyTo().getHost().getAddress()
					+ "/agent-center-dc/rest/agent-center/message/send");
			target.request().post(Entity.entity(replyToMessage, MediaType.APPLICATION_JSON));
		}

	}

}
