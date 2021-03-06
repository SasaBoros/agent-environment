package mdb;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import data.NodeData;
import model.Agent;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/message-queue") })
public class MDBConsumer implements MessageListener {

	@Inject
	private NodeData nodeData;

	@Override
	public void onMessage(Message jmsMessage) {
		ObjectMapper mapper = new ObjectMapper();
		TextMessage textMessage = (TextMessage) jmsMessage;

		try {
			model.ACLMessage message = mapper.readValue(textMessage.getText(), model.ACLMessage.class);
			String agentName = jmsMessage.getStringProperty("agentName");
			employAgent(message, agentName);
		} catch (IOException | JMSException e) {
			e.printStackTrace();
		}

	}

	private void employAgent(model.ACLMessage message, String agentName) {
		for (Agent agent : nodeData.getRunningAgents()) {
			if (agent.getId().getName().equals(agentName)) {
				agent.setNodeData(nodeData);
				agent.handleMessage(message);
				return;
			}
		}

	}

}

