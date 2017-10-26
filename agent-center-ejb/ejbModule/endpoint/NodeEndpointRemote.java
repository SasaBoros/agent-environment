package endpoint;

import java.util.List;

import javax.ejb.Remote;

import model.AgentCenter;

@Remote
public interface NodeEndpointRemote {
	
	void heartbeat();

	void setNodes(List<AgentCenter> nodes);
	
	void registerNode(AgentCenter node);

	void unregisterNode(String nodeAddress);

}


