package endpoints;


import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import service.WSEndpointService;

@ServerEndpoint("/agent-center")
@Stateful
public class WSEndpoint {
	
	@Inject 
	private WSEndpointService wsService;
	
	@OnOpen
	public void open(Session session) {
		System.out.println(session.getId() + " has opened a connection");
		wsService.addClientSession(session);
		wsService.sentDataToClient(session);
	}

	@OnMessage
	public void handleMessage(Session session, String message) {
		wsService.handleMessage(session, message);
	}

	@OnClose
	public void close(Session session) {
		System.out.println(session.getId() + " has closed a connection");
		wsService.removeClientSession(session);
	}

	@OnError
	public void handleError(Session session, Throwable t) {
		
	}
}
