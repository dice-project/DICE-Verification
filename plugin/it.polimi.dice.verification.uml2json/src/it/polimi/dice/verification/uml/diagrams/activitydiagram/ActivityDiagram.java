package it.polimi.dice.verification.uml.diagrams.activitydiagram;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.uml2.uml.Activity;

public class ActivityDiagram{

	private Activity uml_activity;

	public ActivityDiagram(org.eclipse.uml2.uml.Activity uml_activity){
		this.uml_activity=uml_activity;
	}
	
	public String getName(){
		return uml_activity.getName();
	}
	
	public Set<Node> getNodes(){
		Set<Node> nodes=new HashSet<Node>();
		for(org.eclipse.uml2.uml.ActivityNode n: uml_activity.getNodes()){
			nodes.add(NodeFactory.getInstance(n));
		}
		return nodes;
	}
	
	public Set<ControlFlow> getControlFlows(){
		Set<ControlFlow> cfs=new HashSet<ControlFlow>();
		for(org.eclipse.uml2.uml.ActivityEdge uml_cf: uml_activity.getEdges()){
			cfs.add(new ControlFlow(uml_cf));
		}
		return cfs;
	}
	
    public ControlFlow findControlFlow(Node source, Node destination) {
        for (ControlFlow c : this.getControlFlows()) {
            if (c.getSource().equals(source) && c.getDestination().equals(destination)) {
                return c;
            }
        }
        return null;
    }
    
/*
 *     public Set<InterruptibleRegion> getInterruptibleRegions(){
    	Set<InterruptibleRegion> regions=new HashSet<InterruptibleRegion>();
    	for(ActivityGroup ag:this.uml_activity.getGroups()){
    		if(ag instanceof InterruptibleActivityRegion) regions.add(new InterruptibleRegion((InterruptibleActivityRegion)ag));
    	}
    	return regions;
    }
*/
	public String getUMLId() {
		String id=((XMLResource) this.uml_activity.eResource()).getID(uml_activity);
		return id;
	}
	
}
