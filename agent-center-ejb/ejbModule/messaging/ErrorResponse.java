package messaging;

public final class ErrorResponse {
	
	public static final Integer ERRORFREE = 0;
	public static final Integer RECEIVERS_AGENT_TERMINATED = 1;
	public static final Integer SENDER_AGENT_TERMINATED = 2;
	public static final Integer REPLYTO_AGENT_TERMINATED = 3;
	public static final Integer AGENT_NAME_ALREADY_EXISTS = 4;
	public static final Integer AGENT_TYPE_DOESNT_EXIST = 5;
	public static final Integer AGENT_FAILED_TO_START = 6;
	
	public static final String AGENT_NAME_ALREADY_EXISTS_ERROR_TEXT = "Agent with choosen name already exists.";
	public static final String AGENT_TYPE_DOESNT_EXISTS_ERROR_TEXT = "Choosen agent type doesn't exist";
	public static final String AGENT_SUCCESFULLY_STARTED_TEXT = "Agent succesfully started.";
	public static final String AGENT_FAILED_TO_START_TEXT = "Agent failed to start because of unknown reason.";

}
