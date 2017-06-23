package agents;

import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;
import entities.Performative;
import utilities.Util;

@Stateful
public class Ping extends Agent {

	private static final long serialVersionUID = -3168432547521573115L;

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
		
		if(message.getPerformative() == null)
			return;
		
		if (message.getPerformative().equals(Performative.INFORM)) {
			
			if(message.getSender() != null && message.getSender().getType().getName().equals("Pong") && message.getContent() != null) {
				System.out.println("Ping-Pong interactionCount count: " + message.getContent());
			}
		}

	}

}
