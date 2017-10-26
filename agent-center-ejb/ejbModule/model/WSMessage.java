package model;

public class WSMessage {

	private WSMessageType messageType;
	private String content;

	public WSMessage() {
	}

	public WSMessage(WSMessageType messageType, String content) {
		this.messageType = messageType;
		this.content = content;
	}

	public WSMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(WSMessageType messageType) {
		this.messageType = messageType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
