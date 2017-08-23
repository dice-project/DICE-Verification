package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import it.polimi.dice.verification.spark.Transformation;
import it.polimi.dice.verification.uml.helpers.DiceProfileConstants;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public class SparkTransformationNode extends SparkOperationNode {
	
	private Transformation tansformationType;
	
	public SparkTransformationNode(org.eclipse.uml2.uml.OpaqueAction uml_activitynode){
		super(uml_activitynode);
		this.tansformationType = extractTransformationType();
		System.out.println("TransformationNode! - Id: " + this.getId() + " Type:" + this.tansformationType + " duration: " + getDuration());
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
	
}
