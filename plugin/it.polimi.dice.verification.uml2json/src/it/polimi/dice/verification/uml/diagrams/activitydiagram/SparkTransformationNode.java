package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import it.polimi.dice.verification.spark.Partitioner;
import it.polimi.dice.verification.spark.Transformation;
import it.polimi.dice.verification.uml.helpers.DiceProfileConstants;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public class SparkTransformationNode extends SparkOperationNode {
	
	private Transformation tansformationType;
	protected transient Partitioner partitioner = Partitioner.NONE;
	
	public SparkTransformationNode(org.eclipse.uml2.uml.OpaqueAction uml_activitynode){
		super(uml_activitynode);
		this.tansformationType = extractTransformationType();
		this.numTasks = extractNumTasks();
		System.out.println("TransformationNode! - Id: " + this.getId() + " Type:" + this.tansformationType + " duration: " + getDuration());
	}
	
	public Transformation getTransformationType(){
		return tansformationType;
	}
	
	public Partitioner getPartitioner(){
		return partitioner;
	}
	

	private Transformation extractTransformationType(){
		String transformationType_s = (uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_TRANSFORMATION_STEREOTYPE), DiceProfileConstants.SPARK_TRANSFORMATION_TYPE)).toString();
		Transformation transformationType = Transformation.getEnumFromName(transformationType_s);
		return transformationType;
	}
	
	protected Double extractDuration(){
		String duration_s = (String) uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_TRANSFORMATION_STEREOTYPE), DiceProfileConstants.SPARK_OPERATION_DURATION);
		Double duration = duration_s != null ? Double.parseDouble(duration_s) : DEFAULT_DURATION;
		return duration;
	}
	
	protected int extractNumTasks(){
		String numTasks_s = (String) uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_TRANSFORMATION_STEREOTYPE), DiceProfileConstants.SPARK_OPERATION_NUM_TASKS);
		int numTasks = numTasks_s != null ? Integer.parseInt(numTasks_s) : DEFAULT_NUM_TASKS;
		return numTasks;
	}
	
}
