package entities;

public abstract class Agent {
	
	protected AID id;

	public Agent(AID id) {
		this.id = id;
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}
	
	public abstract void handleMessage(Message message);

}
