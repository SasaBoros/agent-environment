package messaging;

public class ErrorResponse {
	
	public static Integer ERRORFREE = 0;
	public static Integer RECEIVERS_AGENT_TERMINATED = 1;
	public static Integer SENDER_AGENT_TERMINATED = 2;
	public static Integer REPLYTO_AGENT_TERMINATED = 3;
	public static Integer AGENT_NAME_ALREADY_EXISTS = 4;
	
	public static String AGENT_NAME_ALREADY_EXISTS_ERROR_TEXT = "Agent with choosen name already exists.";

}
