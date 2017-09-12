package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;
import it.polimi.dice.verification.uml2json.exceptions.NodeNotSupportedException;

public class SparkNodeFactory {

	public static Node getInstance(
			org.eclipse.uml2.uml.ActivityNode uml_activitynode) throws NodeNotSupportedException{
			String nodetype = uml_activitynode.getClass().getName();
			if (uml_activitynode instanceof org.eclipse.uml2.uml.InitialNode) {
				return new InitialNode(
						(org.eclipse.uml2.uml.InitialNode) uml_activitynode);
			}
			/*			if (uml_activitynode instanceof org.eclipse.uml2.uml.ActivityFinalNode) {
				return new FinalNode(
						(org.eclipse.uml2.uml.ActivityFinalNode) uml_activitynode);
			}
			if (uml_activitynode instanceof org.eclipse.uml2.uml.FlowFinalNode) {
				return new FlowFinalNode(
						(org.eclipse.uml2.uml.FlowFinalNode) uml_activitynode);
			}
			if (uml_activitynode instanceof org.eclipse.uml2.uml.ForkNode) {
				return new ForkNode(
						(org.eclipse.uml2.uml.ForkNode) uml_activitynode);
			}
			if (uml_activitynode instanceof org.eclipse.uml2.uml.JoinNode) {
				return new JoinNode(
						(org.eclipse.uml2.uml.JoinNode) uml_activitynode);
			}
			if (uml_activitynode instanceof org.eclipse.uml2.uml.DecisionNode) {
				return new DecisionNode(
						(org.eclipse.uml2.uml.DecisionNode) uml_activitynode);
			}
			if (uml_activitynode instanceof org.eclipse.uml2.uml.MergeNode) {
				return new MergeNode(
						(org.eclipse.uml2.uml.MergeNode) uml_activitynode);
			}
*/		
			if (uml_activitynode instanceof org.eclipse.uml2.uml.OpaqueAction) {
				if (UML2ModelHelper.isSparkTransformation(uml_activitynode)) {
					return new SparkTransformationNode((org.eclipse.uml2.uml.OpaqueAction) uml_activitynode);
				}
				else if(UML2ModelHelper.isSparkAction(uml_activitynode)){
					return new SparkActionNode((org.eclipse.uml2.uml.OpaqueAction) uml_activitynode);
				}				
			}
			throw new NodeNotSupportedException(nodetype);
	}
}

