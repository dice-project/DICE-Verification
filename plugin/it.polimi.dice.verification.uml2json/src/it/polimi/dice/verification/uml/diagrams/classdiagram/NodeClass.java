package it.polimi.dice.verification.uml.diagrams.classdiagram;

import javax.print.attribute.standard.RequestingUserName;

import org.eclipse.uml2.uml.Class;

import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public abstract class NodeClass {

	protected transient org.eclipse.uml2.uml.Class umlClass;
	
	protected String id;

	protected int parallelism;
	
	public NodeClass(org.eclipse.uml2.uml.Class c){
		this.umlClass = c;
		this.id = this.umlClass.getName();
	}
	
	public org.eclipse.uml2.uml.Class getUmlClass(){
		return this.umlClass;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getParallelism() {
		return parallelism;
	}
	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}
	
	protected abstract int extractParallelism();

	

	
}
