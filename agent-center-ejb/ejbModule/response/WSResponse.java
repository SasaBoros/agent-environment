package response;

public class WSResponse {

	private ResponseType responseType;
	private String content;

	public WSResponse() {
	}

	public WSResponse(ResponseType responseType, String content) {
		this.responseType = responseType;
		this.content = content;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
