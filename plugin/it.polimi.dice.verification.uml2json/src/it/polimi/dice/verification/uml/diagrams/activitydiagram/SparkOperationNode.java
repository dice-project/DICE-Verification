package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import it.polimi.dice.verification.json.SparkStage;
import it.polimi.dice.verification.spark.Partitioner;

public abstract class SparkOperationNode extends Node {
	
	protected transient static final double DEFAULT_DURATION = 1.0;
	protected transient static final int DEFAULT_NUM_TASKS = 100;
	
	protected String id;
	
	protected Double duration;
	
	@SerializedName("numtask")
	protected int numTasks;
	
	private transient Set<SparkStage> stages = new HashSet<>();
	
	
	public SparkOperationNode(org.eclipse.uml2.uml.OpaqueAction uml_activitynode){
		this.uml_activitynode = uml_activitynode;
		this.id = this.uml_activitynode.getName();
		this.duration = extractDuration();
	}
	
	public void addStage(SparkStage s){
		stages.add(s);
	}
	
	public Set<SparkStage> getStages(){
		return new HashSet<>(stages);
	}
	
	
	
	public Set<SparkOperationNode> getOutgoingSparkOperations(){
		Set<SparkOperationNode> result = new HashSet<>();
		for (Node n : getOutgoingNodes()) {	// get all successors
			if(n instanceof SparkOperationNode)
				result.add((SparkOperationNode) n);
		}
		return result;
	}

	public Set<SparkOperationNode> getIncomingSparkOperations(){
		Set<SparkOperationNode> result = new HashSet<>();
		for (Node n : getIncomingNodes()) {	// get all predecessors
			if(n instanceof SparkOperationNode)
				result.add((SparkOperationNode) n);
		}
		return result;
	}

	
	public String getId() {
		return id;
	}


	public Double getDuration() {
		return duration;
	}

	
	protected abstract Double extractDuration();
	
	protected abstract int extractNumTasks();

}
