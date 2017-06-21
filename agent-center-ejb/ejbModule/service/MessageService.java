package service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.NodeData;
import entities.AID;
import entities.Agent;
import entities.Message;
import entities.Performative;
import messaging.ErrorResponse;
import utilities.Util;

@Stateless
public class MessageService {

	@Inject
	private NodeData nodeData;

	public Integer handleMessage(Message message) {
		for (AID id : message.getReceivers()) {
			for (Agent agent : nodeData.getRunningAgents()) {
				if (agent.getId().getName().equals(id.getName())) {
					if (agent.getId().getHost().getAddress().equals(System.getProperty(Util.THIS_NODE))) {
						employAgent(message, agent.getId().getName());
					} else {
						try {
							delegateToRecieverAgentNode(message, agent.getId());
						} catch (Exception e) {
							return ErrorResponse.RECEIVERS_AGENT_TERMINATED;
						}
					}
				}
			}
		}
		return ErrorResponse.ERRORFREE;
	}

	private void delegateToRecieverAgentNode(Message message, AID id) {

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client.target("http://" + id.getHost().getAddress()
				+ "/agent-center-dc/rest/agent-center/message/employ-agent/" + id.getName());
		target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
	}

	public void employAgent(Message message, String agentName) {
		try {
			Context context = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
			Connection connection = connectionFactory.createConnection();
			javax.jms.Session session = connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) context.lookup("jms/queue/message-queue");
			connection.start();
			MessageProducer producer = session.createProducer(queue);
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = null;
			try {
				jsonInString = mapper.writeValueAsString(message);
				javax.jms.Message m = session.createTextMessage(jsonInString);
				m.setStringProperty("agentName", agentName);
				producer.send(m);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			connection.stop();
			connection.close();
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Performative[] getPerformatives() {
		return Performative.values();
	}

}
