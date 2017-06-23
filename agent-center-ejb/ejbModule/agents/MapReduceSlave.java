package agents;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.AID;
import entities.Agent;
import entities.Message;
import entities.Performative;
import mdb.MDBProducer;
import utilities.Util;

@Stateful
public class MapReduceSlave extends Agent {

	private static final long serialVersionUID = 7504730369168042742L;

	public MapReduceSlave() {
	}

	@JsonCreator
	public MapReduceSlave(@JsonProperty("id") AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {
		
		System.out.println("MapReduceSlave agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);
		
		if(message.getPerformative() == null)
			return;

		if(message.getPerformative().equals(Performative.REQUEST)) {
			
			Map<Character, Integer> characterCount = new HashMap<Character, Integer>();

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(message.getContent())));
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.replace("\n", "").replace("\r", "").replaceAll("\\s+","");
					for(int  i = 0; i < line.length(); i++) {
						Character ch = line.charAt(i);
						if (characterCount.containsKey(ch)) {
							characterCount.put(ch, characterCount.get(ch) + 1);
						} else {
							characterCount.put(ch, 1);
						}
					}
				}
				ObjectMapper mapper = new ObjectMapper();
				Message reply = new Message();
				reply.setPerformative(Performative.INFORM);
				reply.setSender(id);
				reply.setContent(mapper.writeValueAsString(characterCount));
				AID[] receiver = {message.getSender()};
				reply.setReceivers(Arrays.asList(receiver));
				MDBProducer.sendJMSMessage(reply, message.getSender().getName());
	
				reader.close();
			} catch (IOException e) {
				System.out.println("MapReduceSlave(" + id.getName() + ") agent: Expected text file.");
			}
		}
	}

}
