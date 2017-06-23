package entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import agents.AgentLocal;
import agents.MapReduce;
import agents.Ping;
import agents.Pong;;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = MapReduce.class, name="MapReduce"), @Type(value = Ping.class, name="Ping"), @Type(value = Pong.class, name="Pong") })
public abstract class Agent implements AgentLocal {

	private static final long serialVersionUID = -350569525151189905L;

	protected AID id;
	
	@Override
	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	public abstract void handleMessage(Message message);

}
