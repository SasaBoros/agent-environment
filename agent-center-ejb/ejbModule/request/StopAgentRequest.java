package request;

import java.io.Serializable;

import entities.AID;

public class StopAgentRequest implements Serializable {

	private static final long serialVersionUID = 9142175821448321410L;

	private AID id;

	public StopAgentRequest() {
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

}
