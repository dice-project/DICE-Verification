package it.polimi.dice.verification.json;

import com.google.gson.annotations.SerializedName;

public class JsonVerificationTaskRequest {
	
	private String title;
	@SerializedName("json_context")
	
	private JsonVerificationContext jsonContext;
	
	public JsonVerificationTaskRequest(String title, JsonVerificationContext jsonContext) {
		super();
		this.title = title;
		this.jsonContext = jsonContext;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public JsonVerificationContext getJsonContext() {
		return jsonContext;
	}
	public void setJsonContext(JsonVerificationContext jsonContext) {
		this.jsonContext = jsonContext;
	}
	
	

}
