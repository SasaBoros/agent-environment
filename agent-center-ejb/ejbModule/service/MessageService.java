package service;

import javax.ejb.Stateless;

import entities.Message;
import entities.Performative;
import messaging.ErrorResponse;

@Stateless
public class MessageService {

	public Integer handleMessage(Message message) {
		return ErrorResponse.ERRORFREE;
	}

	public Performative[] getPerformatives() {
		return Performative.values();
	}

}
