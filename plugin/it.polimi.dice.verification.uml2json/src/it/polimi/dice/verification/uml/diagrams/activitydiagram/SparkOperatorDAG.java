package it.polimi.dice.verification.uml.diagrams.activitydiagram;

import java.util.ArrayList;
import java.util.List;


public class SparkOperatorDAG {
	
	private List<SparkOperationNode> operations = new ArrayList<SparkOperationNode>();
	private List<InitialNode> initialNodes = new ArrayList<>();
	
	public List<SparkOperationNode> getOperations(){
		return new ArrayList<SparkOperationNode>(operations);
	}
	
	public List<InitialNode> getInitialNodes(){
		return new ArrayList<InitialNode>(initialNodes);
	}
	
	// TODO use visitor pattern
	public void addNode(Node n){
		if(n instanceof InitialNode)
		{
			initialNodes.add((InitialNode)n);
			System.out.println("Adding " + n.uml_activitynode.getName() + " to InitialNodes");
		}
		else if (n instanceof SparkOperationNode){
			operations.add((SparkOperationNode)n);
			System.out.println("Adding " + n.uml_activitynode.getName() + " to OperationNodes");	
		}
	}
	
	
	

}
