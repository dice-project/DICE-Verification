package it.polimi.dice.verification.json;

import com.google.gson.annotations.SerializedName;

public class JsonVerificationTaskRequest {
	
	private String title;
	@SerializedName("json_context")
	private VerificationJsonContext jsonContext;
	protected String technology;
	
	public JsonVerificationTaskRequest(String title, VerificationJsonContext jsonContext, String technology) {
		super();
		this.title = title;
		this.jsonContext = jsonContext;
		this.technology = technology;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public VerificationJsonContext getJsonContext() {
		return jsonContext;
	}
	public void setJsonContext(VerificationJsonContext jsonContext) {
		this.jsonContext = jsonContext;
	}
	
	public void setTechnology(String technology){
		this.technology = technology;
	}
	
}
