package it.polimi.dice.verification.json;

public class StormVerificationJsonContext extends VerificationJsonContext{
	
/*	@SerializedName(value="app_name")
	private String applicationName;
	private String description;
	private String version;
*/	private StormTopology topology;
/*	@SerializedName(value="verification_params")
	private VerificationParameters verificationParameters;
*/	
	
	public StormVerificationJsonContext(StormTopology t, VerificationParameters vp){
		super(vp);
		this.topology = t;
	}


/*	public String getApplicationName() {
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

*/
	public StormTopology getTopology() {
		return topology;
	}


	public void setTopology(StormTopology topology) {
		this.topology = topology;
	}


/*	public VerificationParameters getVerificationParameters() {
		return verificationParameters;
	}


	public void setVerificationParameters(VerificationParameters verificationParameters) {
		this.verificationParameters = verificationParameters;
	}
*/	

}
