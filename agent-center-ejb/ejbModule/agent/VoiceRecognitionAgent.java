package agent;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Stopwatch;

import data.NodeData;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import processor.WaveAudioProcessor;

public class VoiceRecognitionAgent extends Agent {

	private static final long serialVersionUID = 3795333499292839658L;

	private int slaveCount = 0;
	private transient Map<Integer, String> slaveResults = new TreeMap<Integer, String>();
	private transient Stopwatch stopwatch;

	public VoiceRecognitionAgent() {
	}

	@JsonCreator
	public VoiceRecognitionAgent(@JsonProperty("id") AID id, @JsonProperty("nodeData") NodeData nodeData,
			@JsonProperty("slave") Boolean slave) {
		this.id = id;
		this.nodeData = nodeData;
		this.slave = slave;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		if (message.getPerformative() == null || message.getContent() == null
				|| message.getContent().trim().isEmpty()) {
			System.err.println("Invalid ACL message. Missing content and performative.\n");
			return;
		}

		if (message.getPerformative().equals(Performative.REQUEST)) {
			stopwatch = Stopwatch.createStarted();
			configureCamelRoute(message);
		} else if (message.getPerformative().equals(Performative.INFORM)
				&& message.getSender().getType().getName().equals("VoiceRecognitionAgentSlave")) {

			slaveCount--;
			slaveResults.put(
					Integer.parseInt(
							message.getSender().getName().replaceAll(id.getName(), "").replaceAll("slave", "")),
					message.getContent());
			if (slaveCount == 0) {
				printSlaveResults();
				stopwatch.stop(); // optional
				System.out.println("Time elapsed for myCall() is " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
				slaveResults = new TreeMap<Integer, String>();
			}

		}
	}

	private void configureCamelRoute(ACLMessage message) {

		String[] params = message.getContent().split(";");
		if (params.length != 4) {
			System.err.println(
					"Invalid ACL message. Invalid content format. Expected format: ftp_name;input_location;username;password.\n");
			return;
		}
		VoiceRecognitionAgent agent = this;
		CamelContext context = new DefaultCamelContext();
		try {
			CountDownLatch latch = new CountDownLatch(1);
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("ftp://" + params[0] + "@" + params[1] + "?username=" + params[2] + "&password=" + params[3]
							+ "&delete=true").process(new WaveAudioProcessor(agent, latch));
				}
			});
			context.start();
			latch.await(1, TimeUnit.MINUTES);
			context.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void printSlaveResults() {
		System.out.println("VOICE RECOGNITION RESULT:\n");
		for (Map.Entry<Integer, String> entry : slaveResults.entrySet()) {
			System.out.println(entry.getValue());
		}

	}

	public void setSlaveCount(int slaveCount) {
		this.slaveCount = slaveCount;
	}

}
