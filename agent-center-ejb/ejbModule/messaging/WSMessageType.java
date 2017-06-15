package messaging;

public enum WSMessageType {
	PERFORMATIVES, AGENT_TYPES, RUNNING_AGENTS, START_AGENT, STOP_AGENT, MESSAGE, STARTED_AGENT, ERROR;
	
	public String getWSMessageType() {
        return this.name();
    }
}
