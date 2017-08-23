package it.polimi.dice.verification.uml.diagrams.activitydiagram;

public abstract class SparkOperationNode extends Node {
	
	protected transient static final double DEFAULT_DURATION = 1.0;
	
	protected String id;
	
	protected Double duration;
	
	public SparkOperationNode(org.eclipse.uml2.uml.OpaqueAction uml_activitynode){
		this.uml_activitynode = uml_activitynode;
		this.id = this.uml_activitynode.getName();
		this.duration = extractDuration();
	}
	
	
	public String getId() {
		return id;
	}


	public Double getDuration() {
		return duration;
	}


	protected abstract Double extractDuration();

}
