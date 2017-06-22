package entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentCenter implements Serializable {

	private static final long serialVersionUID = -3641284821727036781L;
	
	private String alias;
	private String address;

	public AgentCenter() {
	}
	
	public AgentCenter(String address) {
		this.address = address;
	}
	
	@JsonCreator
	public AgentCenter(@JsonProperty("alias") String alias, @JsonProperty("address") String address) {
		this.alias = alias;
		this.address = address;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "Node address: " + address + ", alias: " + alias;
	}

}
