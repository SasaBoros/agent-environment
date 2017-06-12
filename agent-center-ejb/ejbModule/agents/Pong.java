package agents;

import entities.AID;
import entities.Agent;
import entities.Message;

public class Pong extends Agent {

	private static final long serialVersionUID = 4443250965720480189L;
	
	public Pong() {
	}
	
	public Pong(AID id) {
		this.id = id;
	}
	
	@Override
	public void handleMessage(Message message) {
		
	}

}
