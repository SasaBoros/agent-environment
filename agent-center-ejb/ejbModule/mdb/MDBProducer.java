package mdb;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.ACLMessage;

public class MDBProducer {
	
	public static void sendJMSMessage(ACLMessage message, String agentName) {
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
			e.printStackTrace();
		}
	}

}



