package messaging;

public enum WSMessageType {
	PERFORMATIVES, AGENT_TYPES, RUNNING_AGENTS, START_AGENT, STOP_AGENT, MESSAGE, STARTED_AGENT, DELETED_AGENT, ERROR, ERROR_FREE;
	
	public String getWSMessageType() {
        return this.name();
    }
}
