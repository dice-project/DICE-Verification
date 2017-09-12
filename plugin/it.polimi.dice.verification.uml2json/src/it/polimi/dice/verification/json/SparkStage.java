package it.polimi.dice.verification.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	

	private int id;
	private Map<Integer, SparkOperationNode> operations = new HashMap<>();
	private transient SparkOperationNode lastOperation;
	private int duration = 0;
	private String name; 
	private Double rate;
	@SerializedName("numtask")
	private int numTask;
	private transient boolean completed = false;
	private List<Integer> parentIds;
	private transient List<SparkStage> predecessors = new ArrayList<>();
	private transient int opCounter = 0;
	
	
	public SparkStage(SparkOperationNode n){
		this.name = "Stage_"+n.getId();
		addOperation(n);
	}
	
	public SparkStage() {
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Map<Integer, SparkOperationNode> getOperations() {
		return new HashMap<Integer, SparkOperationNode>(operations);
	}
	public void setOperations(Map<Integer, SparkOperationNode> operations) {
		this.operations = operations;
	}
	
	public void addOperation(SparkOperationNode op){
		op.addStage(this);
		operations.put(opCounter++, op);
		lastOperation = op;
		duration += op.getDuration();
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
		
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public List<Integer> getParentIds() {
		return parentIds;
	}
	public void setParentIds(List<Integer> parentIds) {
		this.parentIds = parentIds;
	}
	public List<SparkStage> getPredecessors() {
		return new ArrayList<>(predecessors);
	}
	public void setPredecessors(List<SparkStage> predecessors) {
		this.predecessors = predecessors;
	}
	
	public void addPredecessor(SparkStage pred){
		predecessors.add(pred);
		parentIds.add(pred.getId());
	}
	
	public SparkOperationNode getLastOperation(){
		return lastOperation;
	}
	
	public SparkStage duplicate(int id){
		SparkStage dupStage = new SparkStage();
		dupStage.setId(id);
		dupStage.setName("copyOf"+name);
		dupStage.setDuration(duration);
		dupStage.setNumTask(numTask);
		for (SparkOperationNode op : getOperations().values()) {
			op.addStage(dupStage);
		}
		dupStage.setOperations(getOperations());
		dupStage.setPredecessors(predecessors);
		return dupStage;
	}

}
