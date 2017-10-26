package processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.common.base.Stopwatch;

import agent.VoiceRecognitionAgent;
import agent.VoiceRecognitionAgentSlave;
import mdb.MDBProducer;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ACLMessage;
import model.Performative;
import utility.Util;

public class WaveAudioProcessor implements Processor {

	private static final Integer MINIMUM_PART_LENGTH = 10;
	private VoiceRecognitionAgent voiceRecognitonAgent;
	private CountDownLatch latch;

	public WaveAudioProcessor(VoiceRecognitionAgent voiceRecognitonAgent, CountDownLatch latch) {
		this.voiceRecognitonAgent = voiceRecognitonAgent;
		this.latch = latch;
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		AudioInputStream recordingStream = AudioSystem
				.getAudioInputStream(new BufferedInputStream(exchange.getIn().getBody(InputStream.class)));

		byte[] recording = null;
		try {
			recording = IOUtils.toByteArray(recordingStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<byte[]> recordingCuts = new ArrayList<byte[]>();
		
		
		
		double recordingDuration = (recordingStream.getFrameLength() + 0.0)
				/ recordingStream.getFormat().getFrameRate();
		int numberOfCuts;

		Map<String, List<AgentType>> nodeAgentTypes = voiceRecognitonAgent.getNodeData().getNodeAgentTypes();
		List<String> availableNodeAddreses = new ArrayList<String>();
		nodeAgentTypes.keySet().stream().forEach(nodeAddress -> {
			nodeAgentTypes.get(nodeAddress).stream().forEach(agentType -> {
				if (agentType.getName().equals("VoiceRecognitionAgent")
						&& nodeAddress != System.getProperty(Util.THIS_NODE)) {
					availableNodeAddreses.add(nodeAddress);
					return;
				}
			});
		});

		int numberOfNodesNeeded = (int) Math.ceil(recordingDuration / MINIMUM_PART_LENGTH);

		if (numberOfNodesNeeded >= availableNodeAddreses.size() + 1) {
			numberOfCuts = availableNodeAddreses.size() + 1;
		} else {
			numberOfCuts = numberOfNodesNeeded;
		}
		voiceRecognitonAgent.setSlaveCount(numberOfCuts);

		for (int i = 0; i < numberOfCuts - 1; i++) {
			double minVolume = 1;
			int cutLength = 0;
			for (int j = 0; j < 5; j++) {
				byte[] subArray = new byte[1600];
				System.arraycopy(recording, Math.round(recordingStream.getFormat().getSampleRate()) * 5 * 2 - j * 1600,
						subArray, 0, 1600);
				double result = volumeByRMS(subArray);
				if (result < minVolume) {
					minVolume = result;
					cutLength = Math.round(recordingStream.getFormat().getSampleRate()) * 5 * 2 - j * 1600;
				}
			}
			byte[] cut = new byte[cutLength];
			System.arraycopy(recording, 0, cut, 0, cutLength);
			byte[] recordingLeftovers = new byte[recording.length - cutLength];
			System.arraycopy(recording, cutLength, recordingLeftovers, 0, recording.length - cutLength);
			recording = recordingLeftovers;
			recordingCuts.add(cut);
		}
		recordingCuts.add(recording);

		ResteasyClient client = new ResteasyClientBuilder().build();
		
		VoiceRecognitionAgentSlave thisNodeSlaveAgent = new VoiceRecognitionAgentSlave(new AID(
				voiceRecognitonAgent.getId().getName() + "slave0", null, new AgentType("VoiceRecognitionAgentSlave")));
		thisNodeSlaveAgent.setSlave(true);
		voiceRecognitonAgent.getNodeData().addRunningAgent(thisNodeSlaveAgent);
		ACLMessage messageToSlave = new ACLMessage();
		messageToSlave.setPerformative(Performative.REQUEST);
		messageToSlave.setSender(voiceRecognitonAgent.getId());
		messageToSlave.setContent(DatatypeConverter.printBase64Binary(recordingCuts.get(0)));
		MDBProducer.sendJMSMessage(messageToSlave, voiceRecognitonAgent.getId().getName() + "slave0");

		for (int i = 0; i < availableNodeAddreses.size(); i++) {

			ResteasyWebTarget target = client.target("http://" + availableNodeAddreses.get(i)
					+ "/agent-center-dc/rest/agent-center/agent/start/VoiceRecognitionAgentSlave/"
					+ voiceRecognitonAgent.getId().getName() + "slave" + (i + 1) + "/true");
			target.request().async().put(null);

		}
		for (int i = 0; i < availableNodeAddreses.size(); i++) {
			messageToSlave.setContent(DatatypeConverter.printBase64Binary(recordingCuts.get(i + 1)));

			List<AID> receivers = new ArrayList<AID>();
			receivers.add(new AID(voiceRecognitonAgent.getId().getName() + "slave" + (i + 1),
					new AgentCenter(availableNodeAddreses.get(i)), new AgentType("VoiceRecognitionAgentSlave")));
			messageToSlave.setReceivers(receivers);
			client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + availableNodeAddreses.get(i) + "/agent-center-dc/rest/agent-center/message/send");
			target.request().async().post(Entity.entity(messageToSlave, MediaType.APPLICATION_JSON));
		}

		latch.countDown();

	}

	public static double volumeByRMS(byte[] bytes) {
		double[] raw = new double[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			raw[i] = bytes[i] * 0.001;
		}
		double sum = 0d;
		if (raw.length == 0) {
			return sum;
		} else {
			for (int ii = 0; ii < raw.length; ii++) {
				sum += raw[ii];
			}
		}
		double average = sum / raw.length;

		double sumMeanSquare = 0d;
		for (int ii = 0; ii < raw.length; ii++) {
			sumMeanSquare += Math.pow(raw[ii] - average, 2d);
		}
		double averageMeanSquare = sumMeanSquare / raw.length;
		double rootMeanSquare = Math.sqrt(averageMeanSquare);

		return rootMeanSquare;
	}

}
