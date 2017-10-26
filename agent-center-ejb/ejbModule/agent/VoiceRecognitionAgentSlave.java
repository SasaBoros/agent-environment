package agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Stopwatch;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import mdb.MDBProducer;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import utility.Util;

public class VoiceRecognitionAgentSlave extends Agent {

	private static final long serialVersionUID = 873865293989362129L;
	private transient Configuration configuration;
	private transient Stopwatch stopwatch;

	public VoiceRecognitionAgentSlave() {
		loadConfig();
	}

	@JsonCreator
	public VoiceRecognitionAgentSlave(@JsonProperty("id") AID id) {
		this.id = id;
		loadConfig();
	}

	private void loadConfig() {
		configuration = new Configuration();
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);
		configuration.setSampleRate(8000);
	}

	@Override
	public void handleMessage(ACLMessage message) {
		if (message.getPerformative() == null || message.getContent() == null) {
			return;
		}
		
		if (message.getPerformative().equals(Performative.REQUEST)) {
			stopwatch = Stopwatch.createStarted();

			byte[] cut = DatatypeConverter.parseBase64Binary(message.getContent());

			ByteArrayInputStream s = new ByteArrayInputStream(cut);

			StreamSpeechRecognizer recognizer = null;
			try {
				recognizer = new StreamSpeechRecognizer(configuration);
			} catch (IOException e) {
				e.printStackTrace();
			}

			recognizer.startRecognition(s);
			SpeechResult speechResult;
			StringBuilder recognitionResult = new StringBuilder();
			while ((speechResult = recognizer.getResult()) != null) {
				System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
				recognitionResult.append(speechResult.getHypothesis() + "\n");
			}
			recognizer.stopRecognition();
			
			

			ResteasyClient client = new ResteasyClientBuilder().build();

			ACLMessage returnMessage = new ACLMessage();
			returnMessage.setPerformative(Performative.INFORM);
			returnMessage.setContent(recognitionResult.toString());
			returnMessage.setSender(id);
			
			if (System.getProperty(Util.THIS_NODE).equals(message.getSender().getHost().getAddress())) {
				MDBProducer.sendJMSMessage(returnMessage, message.getSender().getName());
			} else {
				List<AID> receivers = new ArrayList<AID>();
				receivers.add(message.getSender());
				returnMessage.setReceivers(receivers);
				ResteasyWebTarget target = client.target("http://" + message.getSender().getHost().getAddress()
						+ "/agent-center-dc/rest/agent-center/message/send");
				target.request().async().post(Entity.entity(returnMessage, MediaType.APPLICATION_JSON));
			}
			stopwatch.stop(); // optional
			System.out.println("slave Time elapsed for myCall() is "+ stopwatch.elapsed(TimeUnit.MILLISECONDS));
			nodeData.removeSlaveAgent(this);
		}
	}

}
