package it.polimi.dice.verification.uml.diagrams.classdiagram;

import org.eclipse.uml2.uml.Class;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;


import com.google.gson.annotations.SerializedName;

public class SpoutClass extends NodeClass{

//	private org.eclipse.uml2.uml.Class umlClass;
//	private String id;
//	private int parallelism;
	@SerializedName(value="avg_emit_rate")
	private Double averageEmitRate;
	

	public SpoutClass(org.eclipse.uml2.uml.Class c) {
			super(c);
			this.averageEmitRate = extractAverageEmitRate();
	}

	public String getName() {
		return super.getUmlClass().getName();
	}
	
	
	/** Do not use if you do not know what are you doing **/
	public Class getUMLSpout(){
		return super.getUmlClass();
	}


	@Override
	public int extractParallelism() {
		//Classifier umlBoltType=umlBolt.getClassifiers().get(0);
		int parallelism= (Integer)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "Spout"), "parallelism");
				//(UML2ModelHelper.getStereotype(umlBoltType, "Bolt"), "parallelism");
		return parallelism;
	}

	public Double extractAverageEmitRate() {
		Double avgEmitRate = (Double)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "Spout"), "AverageEmitRate");
		return avgEmitRate;
	}

		
}
