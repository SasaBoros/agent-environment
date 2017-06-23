package agents;

import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;
import utilities.Util;

@Stateful
public class ContractNet extends Agent {
	
	private static final long serialVersionUID = 5816729639833406085L;

	public ContractNet() {
	}
	
	@JsonCreator
    public ContractNet(@JsonProperty("id") AID id) {
	 this.id = id;
    }
		
	@Override
	public void handleMessage(Message message) {
		System.out.println("ContractNet agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);
	}

}
