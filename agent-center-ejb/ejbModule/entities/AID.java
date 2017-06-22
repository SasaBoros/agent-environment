package entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AID implements Serializable {

	private static final long serialVersionUID = 7310701926118376290L;
	
	private String name;
	private AgentCenter host;
	private AgentType type;
	
	public AID() {
	}
	
	
	@JsonCreator
	public AID(@JsonProperty("name") String name, @JsonProperty("host") AgentCenter host, @JsonProperty("type") AgentType type) {
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
	
	@Override
	public String toString() {
		return "AID name: " + name + ", " + host + ", " + type;
	}

}
