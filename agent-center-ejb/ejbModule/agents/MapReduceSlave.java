package agents;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import entities.AID;
import entities.Agent;
import entities.Message;

public class MapReduceSlave extends Agent {

	private static final long serialVersionUID = 7504730369168042742L;

	private Map<Character, Integer> characterCount = new HashMap<Character, Integer>();

	public MapReduceSlave() {
	}

	@JsonCreator
	public MapReduceSlave(@JsonProperty("id") AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(Message message) {

		if (message.getContent() == null || message.getContent().isEmpty())
			return;

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

			reader.close();
		} catch (IOException e) {
			System.out.println("MapReduceSlave(" + id.getName() + ") agent: Expected text file.");
		}

	}

	public Map<Character, Integer> getCharacterCount() {
		return characterCount;
	}

}
