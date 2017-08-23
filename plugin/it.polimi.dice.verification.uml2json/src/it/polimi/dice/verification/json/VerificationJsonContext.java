package it.polimi.dice.verification.json;

import com.google.gson.annotations.SerializedName;

public class VerificationJsonContext {

	@SerializedName(value="app_name")
	private String applicationName;
	private String description;
	private String version;
	@SerializedName(value="verification_params")
	private VerificationParameters verificationParameters;

	public VerificationJsonContext(/*StormTopology t, */VerificationParameters vp){
//		this.topology = t;
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

	public VerificationParameters getVerificationParameters() {
		return verificationParameters;
	}


	public void setVerificationParameters(VerificationParameters verificationParameters) {
		this.verificationParameters = verificationParameters;
	}


}
