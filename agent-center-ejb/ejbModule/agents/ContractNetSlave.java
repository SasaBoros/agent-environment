package agents;

import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;

@Stateful
public class ContractNetSlave extends Agent {
	

	private static final long serialVersionUID = -108420483562091984L;

	public ContractNetSlave() {
	}
	
	@JsonCreator
    public ContractNetSlave(@JsonProperty("id") AID id) {
	 this.id = id;
    }
	
	@Override
	public void handleMessage(Message message) {
		
		
		
	}

}
