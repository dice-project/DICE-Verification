package it.polimi.dice.verification.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkOperationNode;

public class SparkStage {
/*	{
        "RDDIds": {
            "0": {
                "callsite": "textFile at kmeans.scala:45", 
                "name": "hdfs://10.0.0.5:9000/SparkBench/KMeans/Input"
            }
        }, 
        "cachedRDDs": [
            2
        ], 
        "duration": 51347, 
        "genstage": false, 
        "name": "sum at KMeansModel.scala:87", 
        "nominalrate": 3895066.897773969, 
        "numtask": 1, 
        "parentsIds": [
            0
        ], 
        "recordsread": 100000000, 
        "recordswrite": 0.0, 
        "shufflerecordsread": 0.0, 
        "shufflerecordswrite": 0.0, 
        "skipped": false, 
        "weight": 1.0
    } */
	
	private Map<Integer, SparkOperationNode> operations = new HashMap<>();
	private int duration;
	private String name; 
	private Double rate;
	@SerializedName("numtask")
	private int numTask;
	
	
	public Map<Integer, SparkOperationNode> getOperations() {
		return operations;
	}
	public void setOperations(Map<Integer, SparkOperationNode> operations) {
		this.operations = operations;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public int getNumTask() {
		return numTask;
	}
	public void setNumTask(int numTask) {
		this.numTask = numTask;
	}
	


}
