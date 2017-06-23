package agents;

import java.io.Serializable;

import javax.ejb.Local;

import entities.AID;
import entities.Message;

public interface AgentLocal extends Serializable {

	public void handleMessage(Message message);
	public void setId(AID id);
	public AID getId();
	
}
