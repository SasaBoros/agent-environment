package agents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.ejb.Local;
import javax.ejb.Stateful;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.AgentType;
import entities.Message;
import entities.Performative;
import utilities.Util;

@Stateful
@Local(AgentLocal.class)
public class MapReduce extends Agent {

	private static final long serialVersionUID = -976246058042772369L;

	private Map<Character, Integer> aggregateCharacterCount = new HashMap<Character, Integer>();
	private List<MapReduceSlave> slaves = new ArrayList<MapReduceSlave>();
	

	@Override
	public void handleMessage(Message message) {
		System.out.println("MapReduce agent with name: '" + id.getName() + "' on host: '"
				+ System.getProperty(Util.THIS_NODE) + "' received message: " + message);
		
		if(message.getPerformative() == null) {
			return;
		}

		if (message.getPerformative().equals(Performative.REQUEST)) {
			
			if(message.getContent() == null || message.getContent().isEmpty())
				return;
			
			try (Stream<Path> paths = Files.walk(Paths.get(message.getContent()))) {
				
				aggregateCharacterCount = new HashMap<Character, Integer>();
				slaves = new ArrayList<MapReduceSlave>();
				
				paths.filter(Files::isRegularFile).forEach(path -> {
					MapReduceSlave slave = new MapReduceSlave(new AID("slave-" + path.getFileName(), null, new AgentType("MapReduceSlave")));
					slaves.add(slave);
					
					Message messageToSlave = new Message();
					messageToSlave.setPerformative(Performative.REQUEST);
					messageToSlave.setSender(id);
					messageToSlave.setContent(path.toString());
					AID[] receiver = {slave.getId()};
					message.setReceivers(Arrays.asList(receiver));
					
					slave.handleMessage(messageToSlave);
				});
				

			} catch (IOException e) {
				System.out.println("MapReduce(" + id.getName() + ") agent: Specified folder not found.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (message.getPerformative().equals(Performative.INFORM)) {
			
			if(message.getSender() != null && message.getSender().getType().getName().equals("MapReduceSlave")) {
				removeSlaveAgent(message.getSender().getName());
				ObjectMapper mapper = new ObjectMapper();
				TypeFactory typeFactory = mapper.getTypeFactory();
				MapType mapType = typeFactory.constructMapType(HashMap.class, Character.class, Integer.class);
				try {
					Map<Character, Integer> slaveCharacterCount = mapper.readValue(message.getContent(), mapType);
					addSlaveCharCount(slaveCharacterCount, aggregateCharacterCount);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(slaves.isEmpty()) {
					Character[] characters = sortMap(aggregateCharacterCount);
					printFirst10(aggregateCharacterCount, characters);
				}
			}
			
		}

	}

	private void removeSlaveAgent(String name) {
		for(MapReduceSlave slave : slaves){
			if(slave.getId().getName().equals(name)) {
				slaves.remove(slave);
				break;
			}
		}
	}

	private void printFirst10(Map<Character, Integer> aggregateCharacterCount, Character[] characters) {
		System.out.println("MapReduce(" + id.getName() + ") agent: Top 10 characters: ");
		for (int i = 0; i < characters.length; i++) {
			if (i == 10)
				break;
			Character c = characters[i];
			System.out
					.println((i + 1) + ". Character: '" + c + "', found " + aggregateCharacterCount.get(c) + " times.");
		}

	}

	private Character[] sortMap(Map<Character, Integer> aggregateCharacterCount) {
		Character[] characters = (Character[]) aggregateCharacterCount.keySet()
				.toArray(new Character[aggregateCharacterCount.keySet().size()]);
		Arrays.sort(characters, new Comparator<Character>() {
			public int compare(Character c1, Character c2) {
				return aggregateCharacterCount.get(c2).compareTo(aggregateCharacterCount.get(c1));
			}
		});
		return characters;

	}

	private void addSlaveCharCount(Map<Character, Integer> characterCount,
			Map<Character, Integer> aggregateCharacterCount) {
		for (Character key : characterCount.keySet()) {
			if (aggregateCharacterCount.containsKey(key)) {
				aggregateCharacterCount.put(key, aggregateCharacterCount.get(key) + characterCount.get(key));
			} else {
				aggregateCharacterCount.put(key, characterCount.get(key));
			}
		}

	}

}
