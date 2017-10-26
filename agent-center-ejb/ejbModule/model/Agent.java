package model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import agent.VoiceRecognitionAgent;
import agent.VoiceRecognitionAgentSlave;
import data.NodeData;;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = VoiceRecognitionAgent.class, name = "VoiceRecognitionAgent"),
		@Type(value = VoiceRecognitionAgentSlave.class, name = "VoiceRecognitionAgentSlave") })
public abstract class Agent implements Serializable {

	private static final long serialVersionUID = -350569525151189905L;

	protected AID id;
	protected transient Boolean slave;
	protected transient NodeData nodeData;

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	public abstract void handleMessage(ACLMessage message);

	public Boolean getSlave() {
		return slave;
	}

	public void setSlave(Boolean slave) {
		this.slave = slave;
	}

	public void setNodeData(NodeData nodeData) {
		this.nodeData = nodeData;
	}

	public NodeData getNodeData() {
		return nodeData;
	}
}
