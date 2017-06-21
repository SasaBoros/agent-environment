package agents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;
import utilities.Util;

public class MapReduce extends Agent {

	private static final long serialVersionUID = -976246058042772369L;
	
	public MapReduce() {
	}
	
	@JsonCreator
    public MapReduce(@JsonProperty("id") AID id) {
	 this.id = id;
    }
	
	@Override
	public void handleMessage(Message message) {
		System.out.println("JA SAM MAPREDUCE AGENT NA HOSTU: " + System.getProperty(Util.THIS_NODE));
	}

}
