package agents;

import entities.AID;
import entities.Agent;
import entities.Message;

public class Ping extends Agent {

	private static final long serialVersionUID = -3168432547521573115L;

	public Ping() {
	}
	
	public Ping(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {
		
	}

}
