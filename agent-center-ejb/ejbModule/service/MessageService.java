package service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import mdb.MDBProducer;
import model.AID;
import model.ErrorResponse;
import model.ACLMessage;
import model.Performative;
import utility.Util;

@Stateless
public class MessageService {

	public Response handleMessage(ACLMessage message) {
		for (AID id : message.getReceivers()) {
			try {
				if (id.getHost().getAddress().equals(System.getProperty(Util.THIS_NODE))) {
					MDBProducer.sendJMSMessage(message, id.getName());
				} else {
					try {
						delegateToRecieverAgentNode(message, id);
					} catch (Exception e) {
						return Response.serverError().entity(ErrorResponse.RECEIVER_AGENT_TERMINATED).build();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Response.ok().build();
	}

	private void delegateToRecieverAgentNode(ACLMessage message, AID id) {
		List<AID> receivers = message.getReceivers();
		AID[] receiver = { id };
		message.setReceivers(Arrays.asList(receiver));

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target("http://" + id.getHost().getAddress() + "/agent-center-dc/rest/agent-center/message/send");
		target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
		message.setReceivers(receivers);
	}

	public Performative[] getPerformatives() {
		return Performative.values();
	}

}
