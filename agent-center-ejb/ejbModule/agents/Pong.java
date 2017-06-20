package agents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;

public class Pong extends Agent {

	private static final long serialVersionUID = 4443250965720480189L;
	
	public Pong() {
	}
	
	@JsonCreator
    public Pong(@JsonProperty("id") AID id) {
	 this.id = id;
    }
	
	@Override
	public void handleMessage(Message message) {
		
	}

}
