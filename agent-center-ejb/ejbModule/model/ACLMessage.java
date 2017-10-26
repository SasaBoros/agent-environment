package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ACLMessage implements Serializable {

	private static final long serialVersionUID = 8313865352707645861L;

	private Performative performative;
	private AID sender;
	private List<AID> receivers;
	private AID replyTo;
	private String content;
	private Object contentObj;
	private Map<String, Object> userArgs;
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	private Long replyBy;

	public ACLMessage() {
	}
	
	@JsonCreator
	public ACLMessage(@JsonProperty("performative")Performative performative, @JsonProperty("sender")AID sender, @JsonProperty("receivers")List<AID> receivers, @JsonProperty("replyTo")AID replyTo, @JsonProperty("content")String content,
			@JsonProperty("language")String language, @JsonProperty("encoding")String encoding, @JsonProperty("ontology")String ontology, @JsonProperty("protocol")String protocol, @JsonProperty("conversationId")String conversationId, @JsonProperty("replyWith")String replyWith,
			@JsonProperty("inReplyTo")String inReplyTo, @JsonProperty("replyBy")Long replyBy) {
		this.performative = performative;
		this.sender = sender;
		this.receivers = receivers;
		this.replyTo = replyTo;
		this.content = content;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}

	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public AID getSender() {
		return sender;
	}

	public void setSender(AID sender) {
		this.sender = sender;
	}

	public List<AID> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<AID> receivers) {
		this.receivers = receivers;
	}

	public AID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getContentObj() {
		return contentObj;
	}

	public void setContentObj(Object contentObj) {
		this.contentObj = contentObj;
	}

	public Map<String, Object> getUserArgs() {
		return userArgs;
	}

	public void setUserArgs(Map<String, Object> userArgs) {
		this.userArgs = userArgs;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public Long getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}

	@Override
	public String toString() {
		StringBuilder message = new StringBuilder();
		message.append("Message { performative: " + performative + ", sender: " + ((sender == null) ? "" : sender.getName()) + ", receivers: ");
		if(receivers != null) {
			for (AID reciever : receivers) {
				message.append(reciever.getName() + ", ");
			}
		}
			
		message.append("replyTo: " + ((replyTo == null) ? "" : replyTo.getName()) + ", content: " + content + ", language: " + language
				+ ", encoding: " + encoding + ", ontology: " + ontology + ", protocol: " + protocol
				+ ", conversationId: " + conversationId + ", replyWith: " + replyWith + ", inReplyTo: " + inReplyTo
				+ ", replyBy: " + replyBy + " }");
		return message.toString();
	}

}
