package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import it.polimi.dice.verification.spark.Action;
import it.polimi.dice.verification.uml.helpers.DiceProfileConstants;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public class SparkActionNode extends SparkOperationNode {
	
	private Action actionType;
	
	
	public SparkActionNode(org.eclipse.uml2.uml.OpaqueAction uml_activitynode){
		super(uml_activitynode);
		this.actionType = extractTransformationType();
		this.numTasks = extractNumTasks();
		System.out.println("ACTION NODE! - Id: " + this.getId() + " Type: " + this.actionType + " duration: " + getDuration());

		
	}
	
	private Action extractTransformationType(){
		String actionType_s = (uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_ACTION_STEREOTYPE), DiceProfileConstants.SPARK_ACTION_TYPE)).toString();
		Action actionType = Action.getEnumFromName(actionType_s);
		return actionType;
	}


	protected Double extractDuration(){
		String duration_s = (String) uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_ACTION_STEREOTYPE), DiceProfileConstants.SPARK_OPERATION_DURATION);
		Double duration = duration_s != null ? Double.parseDouble(duration_s) : DEFAULT_DURATION;
		return duration;
	}

	protected int extractNumTasks(){
		String numTasks_s = (String) uml_activitynode.getValue(UML2ModelHelper.getStereotype(uml_activitynode, DiceProfileConstants.SPARK_ACTION_STEREOTYPE), DiceProfileConstants.SPARK_OPERATION_NUM_TASKS);
		int numTasks = numTasks_s != null ? Integer.parseInt(numTasks_s) : DEFAULT_NUM_TASKS;
		return numTasks;
	}
}
