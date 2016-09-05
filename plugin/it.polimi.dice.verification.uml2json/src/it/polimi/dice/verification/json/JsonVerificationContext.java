package it.polimi.dice.verification.json;

import com.google.gson.annotations.SerializedName;
import it.polimi.dice.verification.json.StormTopology;

public class JsonVerificationContext {
	
	@SerializedName(value="app_name")
	private String applicationName;
	private String description;
	private String version;
	private StormTopology topology;
	@SerializedName(value="verification_params")
	private VerificationParameters verificationParameters;
	
	
	public JsonVerificationContext(StormTopology t, VerificationParameters vp){
		this.topology = t;
		this.verificationParameters = vp;
	}


	public String getApplicationName() {
		return applicationName;
	}


	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public StormTopology getTopology() {
		return topology;
	}


	public void setTopology(StormTopology topology) {
		this.topology = topology;
	}


	public VerificationParameters getVerificationParameters() {
		return verificationParameters;
	}


	public void setVerificationParameters(VerificationParameters verificationParameters) {
		this.verificationParameters = verificationParameters;
	}
	

}
