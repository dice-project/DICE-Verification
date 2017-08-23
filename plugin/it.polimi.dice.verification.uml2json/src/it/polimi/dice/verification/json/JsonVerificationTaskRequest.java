package it.polimi.dice.verification.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class JsonVerificationTaskRequest {
	
	private String title;
	@SerializedName("json_context")
	
	private VerificationJsonContext jsonContext;
	
	public JsonVerificationTaskRequest(String title, VerificationJsonContext jsonContext) {
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
	public VerificationJsonContext getJsonContext() {
		return jsonContext;
	}
	public void setJsonContext(VerificationJsonContext jsonContext) {
		this.jsonContext = jsonContext;
	}
	
	

	public static void main(String[] args) {
		StormTopology t = new StormTopology();
		VerificationParameters vp = new StormVerificationParameters();
		
		SparkVerificationJsonContext sc = new SparkVerificationJsonContext(vp);
		
		JsonVerificationTaskRequest tr = new JsonVerificationTaskRequest("Ciao", sc);
		
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(tr));
		
	}
}
