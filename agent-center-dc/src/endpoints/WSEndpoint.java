package endpoints;


import java.io.IOException;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import agents.Ping;
import data.AgentData;
import entities.AID;
import entities.Message;
import entities.Performative;
import messaging.WSMessage;
import messaging.WSMessageType;
import utilities.Util;

@ServerEndpoint("/agent-center")
@Singleton
public class WSEndpoint {
	@OnOpen
	public void open(Session session) {
		System.out.println(session.getId() + " has opened a connection");
		ObjectMapper mapper = new ObjectMapper();
		try {
			session.getBasicRemote().sendText(mapper.writeValueAsString(new WSMessage(WSMessageType.PERFORMATIVES, mapper.writeValueAsString(Performative.values()))));
			session.getBasicRemote().sendText(mapper.writeValueAsString(new WSMessage(WSMessageType.AGENT_TYPES, mapper.writeValueAsString(AgentData.getAgentTypes()))));
			session.getBasicRemote().sendText(mapper.writeValueAsString(new WSMessage(WSMessageType.RUNNING_AGENTS, mapper.writeValueAsString(AgentData.getRunningAgents()))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("AAAAAAAAAAAAAAAAAAAAAA");
			e.printStackTrace();
		}
		
		System.out.println(Util.getMasterNodePort());
		
	}

	@OnMessage
	public void processMessage(Session session, String message, boolean last) {
		ObjectMapper mapper = new ObjectMapper();
		WSMessage wsMessage = null;
		try {
			wsMessage = mapper.readValue(message, WSMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(wsMessage == null)
			return;
		
		if(wsMessage.getMessageType().equals(WSMessageType.START_AGENT)) {
			System.out.println(wsMessage.getContent());
			try {
				session.getBasicRemote().sendText(mapper.writeValueAsString(new WSMessage(WSMessageType.STARTED_AGENT, mapper.writeValueAsString(new Ping(new AID(mapper.readValue(wsMessage.getContent(), String.class).split("/")[0]))))));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(wsMessage.getMessageType().equals(WSMessageType.MESSAGE)) {
			
			System.out.println(wsMessage.getContent());
			try {
				Message m = mapper.readValue(wsMessage.getContent(), Message.class);
				System.out.println(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		ObjectMapper mapper = new ObjectMapper();
//        WSMessage wsMessage = null;
//		try {
//			wsMessage = mapper.readValue(message, WSMessage.class);
//			if(wsMessage.getType().equals(Util.LOAD)) {
//				session.getBasicRemote().sendText(mapper.writeValueAsString(new AgentResponse()));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}

	@OnClose
	public void close(Session session) {
		System.out.println(session.getId() + " has closed a connection");
	}

	@OnError
	public void handleError(Session session, Throwable t) {
	}
}
