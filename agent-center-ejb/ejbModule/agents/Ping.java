package agents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;

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
		
	}

}
