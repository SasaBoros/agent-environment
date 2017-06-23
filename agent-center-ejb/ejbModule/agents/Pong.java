package agents;

import java.util.Arrays;

import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;
import entities.Performative;
import mdb.MDBProducer;
import utilities.Util;

@Stateful
public class Pong extends Agent {

	private static final long serialVersionUID = 4443250965720480189L;

	private Integer pingPongCount = 0;

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

		processMessage(message);
	}

	private void processMessage(Message message) {
		
		if(message.getPerformative() == null)
			return;

		if (message.getPerformative().equals(Performative.REQUEST)) {
			
			if (message.getReplyTo() != null && message.getReplyTo().getType().getName().equals("Ping")) {

				Message replyToMessage = new Message();
				replyToMessage.setPerformative(Performative.INFORM);
				replyToMessage.setSender(id);
				replyToMessage.setContent((++pingPongCount).toString());
				replyToMessage.setReplyTo(id);
				AID[] receiver = { message.getReplyTo() };
				replyToMessage.setReceivers(Arrays.asList(receiver));
	
				if (message.getReplyTo().getHost().equals(System.getProperty(Util.THIS_NODE))) {
					MDBProducer.sendJMSMessage(replyToMessage, message.getReplyTo().getName());
				} else {
	
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://" + message.getReplyTo().getHost().getAddress()
							+ "/agent-center-dc/rest/agent-center/message/send");
					target.request().post(Entity.entity(replyToMessage, MediaType.APPLICATION_JSON));
				}
			}
		}
	}

}
