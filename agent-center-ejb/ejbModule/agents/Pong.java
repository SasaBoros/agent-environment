package agents;

import java.util.Arrays;

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

public class Pong extends Agent {

	private static final long serialVersionUID = 4443250965720480189L;

	@Inject
	private NodeData nodeData;

	public Pong() {
	}

	@JsonCreator
	public Pong(@JsonProperty("id") AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {
		System.out.println("Pong agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);

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
