package endpoints;


import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/agent-center")
@Singleton
public class WSEndpoint {
	@OnOpen
	public void open(Session session) {
		System.out.println(session.getId() + " has opened a connection");
	}

	@OnMessage
	public void processMessage(Session session, String message, boolean last) {
		System.out.println(message);
	}

	@OnClose
	public void close(Session session) {
		System.out.println(session.getId() + " has closed a connection");
	}

	@OnError
	public void handleError(Session session, Throwable t) {
	}
}
