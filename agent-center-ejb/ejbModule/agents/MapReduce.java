package agents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.AgentType;
import entities.Message;
import utilities.Util;

public class MapReduce extends Agent {

	private static final long serialVersionUID = -976246058042772369L;
	
	@Inject
	private NodeData nodeData;

	public MapReduce() {
	}

	@JsonCreator
	public MapReduce(@JsonProperty("id") AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {
		System.out.println("MapReduce agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);
		
		Map<Character, Integer> aggregateCharacterCount = new HashMap<Character, Integer>();
		
		try (Stream<Path> paths = Files.walk(Paths.get(message.getContent()))) {
			paths
			.filter(Files::isRegularFile)
			.forEach(path -> {
				MapReduceSlave slave = new MapReduceSlave(new AID("slave", null, new AgentType("MapReduceSlave")));
				
				Message messageToSlave = new Message();
				messageToSlave.setSender(id);
				messageToSlave.setContent(path.toString());
				slave.handleMessage(messageToSlave);
				addSlaveCharCount(slave.getCharacterCount() , aggregateCharacterCount);
			});
			
			Character[] characters = sortMap(aggregateCharacterCount);
			printFirst10(aggregateCharacterCount, characters);
			
		} catch(IOException e) {
			System.out.println("MapReduce(" + id.getName() + ") agent: Specified folder not found.");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		replyTo(message);
		
		
	}


	private void printFirst10(Map<Character, Integer> aggregateCharacterCount, Character[] characters) {
		System.out.println("MapReduce(" + id.getName() + ") agent: Top 10 characters: ");
		for(int i = 0; i < characters.length; i++) {
			if(i == 10)
				break;
			Character c = characters[i];
			System.out.println((i+1) + ". Character: '" + c + "', found " + aggregateCharacterCount.get(c) +" times.");
		}
		
	}

	private Character[] sortMap(Map<Character, Integer> aggregateCharacterCount) {
		Character[] characters = (Character[]) aggregateCharacterCount.keySet().toArray(new Character[aggregateCharacterCount.keySet().size()]);
		Arrays.sort(characters, new Comparator<Character>() {
		    public int compare(Character c1, Character c2) {
		        return aggregateCharacterCount.get(c2).compareTo(
		               aggregateCharacterCount.get(c1));
		    }
		});
		return characters;
		
	}

	private void addSlaveCharCount(Map<Character, Integer> characterCount, Map<Character, Integer> aggregateCharacterCount) {
		for(Character key : characterCount.keySet()) {
			if(aggregateCharacterCount.containsKey(key)) {
				aggregateCharacterCount.put(key, aggregateCharacterCount.get(key) + characterCount.get(key));
			}
			else {
				aggregateCharacterCount.put(key, characterCount.get(key));
			}
		}
		
	}

	private void replyTo(Message message) {
		if(message.getReplyTo() == null || message.getReplyTo().getName().equals(id.getName()))
			return;
		
		if(message.getReplyTo().getHost().equals(System.getProperty(Util.THIS_NODE))) {
			for(Agent agent : nodeData.getRunningAgents()) {
				if(agent.getId().getName().equals(message.getReplyTo().getName())) {
					agent.handleMessage(message);
					break;
				}
			}
		}
		else {
			Message replyToMessage = new Message();
			replyToMessage.setSender(id);
			AID[] receiver = {message.getReplyTo()};
			replyToMessage.setReceivers(Arrays.asList(receiver));
			
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://" + message.getReplyTo().getHost().getAddress()
					+ "/agent-center-dc/rest/agent-center/message/send");
			target.request().post(Entity.entity(replyToMessage, MediaType.APPLICATION_JSON));
		}
	}
}
