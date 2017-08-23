package it.polimi.dice.verification.uml.diagrams.classdiagram;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;

import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;

public class ClassDiagram {

	@SuppressWarnings("unused")
	private Logger LOGGER = Logger.getLogger(ClassDiagram.class);
	
	private Package uml_sys_package;
	
	public ClassDiagram(org.eclipse.uml2.uml.Package system){
		this.uml_sys_package=system;
	}
	
	public Set<Class> getClasses(){
		HashSet<Class> classes=new HashSet<Class>();
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(c instanceof org.eclipse.uml2.uml.Class){
				classes.add(new Class((org.eclipse.uml2.uml.Class)c));
			}
		}
		return classes;
	}
	
	public Set<Spout> getSpouts() {
		HashSet<Spout> spouts=new HashSet<Spout>();
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(UML2ModelHelper.isSpoutInstance(c)){
				spouts.add(new Spout((org.eclipse.uml2.uml.InstanceSpecification)c));
			}
		}
		return spouts;
	}

	public Set<Bolt> getBolts() {
		HashSet<Bolt> bolts=new HashSet<Bolt>();
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(UML2ModelHelper.isBoltInstance(c)){
				bolts.add(new Bolt((org.eclipse.uml2.uml.InstanceSpecification)c));
			}
		}
		return bolts;
	}
	

	public Class findClass(String name){
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(c instanceof org.eclipse.uml2.uml.Class){
				org.eclipse.uml2.uml.Class myc=(org.eclipse.uml2.uml.Class)c;
				if(myc.getName().equals(name)) return new Class(myc);
			}
		}
		return null;
	}
	
	public Set<Object> getObjects(){
		HashSet<Object> objects=new HashSet<Object>();
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(c instanceof org.eclipse.uml2.uml.InstanceSpecification){
				org.eclipse.uml2.uml.InstanceSpecification is=(org.eclipse.uml2.uml.InstanceSpecification) c;
				//Association instances are threated like instance specifications. 
				//I don't want them in my objects set
				if(!(is.getClassifiers().iterator().next() instanceof org.eclipse.uml2.uml.Association)) objects.add(new Object((org.eclipse.uml2.uml.InstanceSpecification)c));
			}
		}
		return objects;
	}
	
	public Object findObject(String name){
		for(Element c: this.uml_sys_package.getOwnedElements()){
			if(c instanceof org.eclipse.uml2.uml.InstanceSpecification){
				org.eclipse.uml2.uml.InstanceSpecification myobj=(org.eclipse.uml2.uml.InstanceSpecification) c;
				if(myobj.getName().equals(name)) return new Object((org.eclipse.uml2.uml.InstanceSpecification)c);
			}
		}
		return null;
	}

	
}
