package request;

import java.io.Serializable;

import entities.AgentType;

public class StartAgentRequest implements Serializable {

	private static final long serialVersionUID = 2588521671592836813L;
	
	private String name;
	private AgentType type;

	public StartAgentRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}

}
