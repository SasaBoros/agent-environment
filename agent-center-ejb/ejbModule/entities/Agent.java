package entities;

import java.io.Serializable;

public abstract class Agent implements Serializable {
	
	private static final long serialVersionUID = -350569525151189905L;
	
	protected AID id;

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}
	
	public abstract void handleMessage(Message message);

}
