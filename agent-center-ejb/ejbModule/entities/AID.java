package entities;

import java.io.Serializable;

public class AID implements Serializable {

	private static final long serialVersionUID = 7310701926118376290L;
	
	private String name;
	private AgentCenter host;
	private AgentType type;
	
	public AID() {
	}
	
	public AID(String name) {
		this.name = name;
	}
	
	public AID(String name, AgentType type) {
		this.name = name;
		this.type = type;
	}

	public AID(String name, AgentCenter host, AgentType type) {
		this.name = name;
		this.host = host;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentCenter getHost() {
		return host;
	}

	public void setHost(AgentCenter host) {
		this.host = host;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}

}
