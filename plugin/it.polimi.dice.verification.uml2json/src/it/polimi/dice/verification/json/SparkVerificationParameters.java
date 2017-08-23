package it.polimi.dice.verification.json;

import com.google.gson.annotations.SerializedName;

public class SparkVerificationParameters extends VerificationParameters{

	/*
	 * TARGET JSON REPRESENTATION
	"verification_params": {
	    "no_loops": true, 
	    "parametric_tc": true,
	    "plugin": "ae2sbvzot", 	// inherited from parent
	    "time_bound": 50		// inherited from parent
	}*/
	
	@SerializedName("no_loops")
	boolean noLoops;
	@SerializedName("parametric_tc")
	boolean parametricTc;
	
	public SparkVerificationParameters(boolean noLoops, boolean parametricTc) {
		super();
		this.noLoops = noLoops;
		this.parametricTc = parametricTc;
	}
	
	public boolean isNoLoops() {
		return noLoops;
	}
	public void setNoLoops(boolean noLoops) {
		this.noLoops = noLoops;
	}
	public boolean isParametricTc() {
		return parametricTc;
	}
	public void setParametricTc(boolean parametricTc) {
		this.parametricTc = parametricTc;
	}	
	
}

