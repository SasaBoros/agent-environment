package service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import entities.AID;
import entities.Message;
import entities.Performative;
import mdb.MDBProducer;
import messaging.ErrorResponse;
import utilities.Util;

@Stateless
public class MessageService {

	public Integer handleMessage(Message message) {
		
		for (AID id : message.getReceivers()) {
			try{
			if (id.getHost().getAddress().equals(System.getProperty(Util.THIS_NODE))) {
				MDBProducer.sendJMSMessage(message, id.getName());
			} else {
				try {
					delegateToRecieverAgentNode(message, id);
				} catch (Exception e) {
					return ErrorResponse.RECEIVERS_AGENT_TERMINATED;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		}
		
		return ErrorResponse.ERRORFREE;
	}

	private void delegateToRecieverAgentNode(Message message, AID id) {
		List<AID> receivers = message.getReceivers();
		AID[] receiver = {id};
		message.setReceivers(Arrays.asList(receiver));

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client.target("http://" + id.getHost().getAddress()
				+ "/agent-center-dc/rest/agent-center/message/send");
		target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
		message.setReceivers(receivers);
	}

	public Performative[] getPerformatives() {
		return Performative.values();
	}

}
