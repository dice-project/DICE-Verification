package it.polimi.dice.verification.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class SparkVerificationJsonContext extends VerificationJsonContext {

/*  "analysis_type": "feasibility", 
    "app_name": "kmeans_1_by20_c64_t50_no_l_d85844_tc_by20_n_rounds_by2",
    "deadline": 85844, 
    "max_time": 85844, 
    "stages": {}
    "tolerance": 0.0, 
    "tot_cores": 1
    "verification_params"
    */

	@SerializedName("analysis_type")
	String analysisType;
	int deadline;
	@SerializedName("max_time")
	int maxTime;
	Double tolerance;
	@SerializedName("tot_cores")
	int totCores;
	
	//List<SparkStage> stages;
	Map<Integer, SparkStage> stages = new HashMap<>();
	

	public SparkVerificationJsonContext(VerificationParameters vp, String analysisType, int deadline,
			Double tolerance, int totCores, Map<Integer, SparkStage> stages) {
		super(vp);
		this.analysisType = analysisType;
		this.deadline = deadline;
		this.maxTime = deadline;
		this.tolerance = tolerance;
		this.totCores = totCores;
		this.stages = stages;
	}

	
}
