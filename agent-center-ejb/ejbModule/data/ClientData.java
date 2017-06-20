package data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.websocket.Session;

@Singleton
public class ClientData {

	private List<Session> clientSessions = new ArrayList<Session>();

	public void setClientSessions(List<Session> clientSessions) {
		this.clientSessions = clientSessions;
	}

	public List<Session> getClientSessions() {
		return clientSessions;
	}

	public void removeClientSession(Session clientSession) {
		clientSessions.remove(clientSession);
	}
	
	public void addClientSession(Session clientSession) {
		clientSessions.add(clientSession);
	}
}
