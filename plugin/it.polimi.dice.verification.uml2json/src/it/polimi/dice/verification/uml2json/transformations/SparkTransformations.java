package it.polimi.dice.verification.uml2json.transformations;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import it.polimi.dice.core.util.Stack;
import it.polimi.dice.verification.json.SparkStage;
import it.polimi.dice.verification.spark.Partitioner;
import it.polimi.dice.verification.spark.ShuffleEffect;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.InitialNode;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.Node;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkActionNode;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkNodeFactory;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkOperationNode;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkOperatorDAG;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkTransformationNode;
import it.polimi.dice.verification.uml2json.exceptions.InvalidDAGConnectionException;
import it.polimi.dice.verification.uml2json.exceptions.NodeNotSupportedException;

public class SparkTransformations {

	public static SparkOperatorDAG getOperationsDAGFromUmlFile(File umlFile) {
		SparkOperatorDAG dag = new SparkOperatorDAG();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource umlResource = resourceSet.getResource(URI.createFileURI(umlFile.getAbsolutePath()), true);
		// EList<EObject> inObjects = umlResource.getContents();
		for (Iterator<EObject> it = umlResource.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();
			if (eObject instanceof org.eclipse.uml2.uml.ActivityNode) {
				try {
					Node n = SparkNodeFactory.getInstance((org.eclipse.uml2.uml.ActivityNode) eObject);
					dag.addNode(n);
				} catch (NodeNotSupportedException e) {
					System.out.println("Parsing unsupported node type (" + e.getMessage() + ").. Skipping it.");
				}
			}
		}
		return dag;
	}

	public static Map<Integer, SparkStage> getExecutionDAGFromOperatorDAG(SparkOperatorDAG opDag) {

		int stageCounter = 0;
		Map<Integer, SparkStage> result = new HashMap<>();

		Set<SparkOperationNode> visitedNodes = new HashSet<>();
		Stack<SparkOperationNode> nodesToBeVisited = new Stack<>();
		Stack<SparkOperationNode> tmpSuccessors = new Stack<>();
		for (InitialNode i : opDag.getInitialNodes()) {
			for (Node n : i.getOutgoingNodes()) {
				if (n instanceof SparkOperationNode) {
					nodesToBeVisited.push((SparkOperationNode) n);
				}
			}
		}
		// Continue until all nodes have been visited
		while (!nodesToBeVisited.isEmpty()) {
			SparkOperationNode currentNode = nodesToBeVisited.pop();
			if (!visitedNodes.contains(currentNode)) { // visit new node
				System.out.println("Visiting " + currentNode.getId() + "...");
				processNode(currentNode, result, stageCounter);
				visitedNodes.add(currentNode);
				// get alsuccessors
				for (SparkOperationNode n : currentNode.getOutgoingSparkOperations()) { 
					tmpSuccessors.push(n);
				}
				while (!tmpSuccessors.isEmpty()) {
					SparkOperationNode currentSuccessor = tmpSuccessors.pop();

					if (allPredecessorsVisited(currentSuccessor, visitedNodes))
						nodesToBeVisited.push(currentSuccessor);
				}
			}
		}
		
		return result;

	}

	private static void processNode(SparkOperationNode currentNode, Map<Integer, SparkStage> stagesMap,
			int currentStageCounter) {
		Set<SparkOperationNode> predecessors = currentNode.getIncomingSparkOperations();
		if (predecessors.isEmpty())
			createStageWithCurrentNode(currentStageCounter, currentNode, stagesMap);
		else for (SparkOperationNode pred : predecessors) {
			if(currentNode instanceof SparkTransformationNode && pred instanceof SparkTransformationNode)
				evaluateConnection((SparkTransformationNode)currentNode, (SparkTransformationNode)pred, stagesMap, currentStageCounter);
			
		}
	}

	private static void createStageWithCurrentNode(int currentStageCounter, SparkOperationNode currentNode,
			Map<Integer, SparkStage> stagesMap) {
		if (currentNode.getStages().isEmpty()) {
			SparkStage stage = new SparkStage(currentNode);
			insertStageWithNewID(stagesMap, stage);
		}
	}

	private static void evaluateConnection(SparkOperationNode current, SparkOperationNode predecessor,
			Map<Integer, SparkStage> stages, int currentStageCounter) {
		
		Set<SparkStage> incompleteStages = getIncompleteStagesEndingWithOp(stages, predecessor);
		SparkStage incompleteStage = null;
		if (incompleteStages.iterator().hasNext())
			 incompleteStage = incompleteStages.iterator().next();

		if(predecessor instanceof SparkActionNode)
			throw new InvalidDAGConnectionException("Action cannot have outgoing edges.");
		
		if(current instanceof SparkActionNode){
			incompleteStage.addOperation(current);
			incompleteStage.setCompleted(true);
			return;
		}
		
		if(predecessor instanceof SparkTransformationNode && current instanceof SparkTransformationNode){
			if (isShuffleNeeded((SparkTransformationNode) current, (SparkTransformationNode) predecessor)) {
				if (incompleteStage != null)
					incompleteStage.setCompleted(true);
				createStageWithCurrentNode(currentStageCounter, current, stages);
			}
			else{
				if (incompleteStage != null)
					incompleteStage.addOperation(current);
				else{
					createStageWithCurrentNode(currentStageCounter, current, stages);
				}
			}
		}
		

	}

	private static boolean isShuffleNeeded(SparkTransformationNode current, SparkTransformationNode predecessor){
		if (current.getTransformationType().getShuffleEffect().equals(ShuffleEffect.SHUFFLE)
		|| (current.getTransformationType().getShuffleEffect().equals(ShuffleEffect.DEPENDS_ON_PARENTS)
				&& predecessor.getPartitioner().equals(Partitioner.NONE)))
			return true;
		return false;
	}
	
	private static boolean allPredecessorsVisited(SparkOperationNode node, Set<SparkOperationNode> visited) {
		boolean allVisited = true;
		Iterator iterator = node.getIncomingSparkOperations().iterator();
		while (iterator.hasNext() && allVisited) {
			allVisited = visited.contains(iterator.next());
		}
		return allVisited;
	}

	public static void insertStageWithNewID(Map<Integer, SparkStage> map, SparkStage stage){
		int max = 0;
		for (int i : map.keySet()) {
			if(i > max)
				max = i;
		}
		stage.setId(max + 1); 
		map.put(max + 1, stage);
	}
	
	/**
	 * 
	 * @return the set of stages which are not completed and end with the current Operation
	 */
	public static Set<SparkStage> getIncompleteStagesEndingWithOp(Map<Integer, SparkStage> map, SparkOperationNode op){
		Set<SparkStage> result = new HashSet<>();
		for (SparkStage sparkStage : map.values())
			if(!sparkStage.isCompleted() && sparkStage.getLastOperation().equals(op))
				result.add(sparkStage);
		return result;
	}
	

}
