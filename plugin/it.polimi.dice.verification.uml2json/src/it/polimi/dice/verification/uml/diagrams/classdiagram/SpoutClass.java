package it.polimi.dice.verification.uml.diagrams.classdiagram;

import org.eclipse.uml2.uml.Class;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;


import com.google.gson.annotations.SerializedName;

public class SpoutClass extends NodeClass{

	private transient static final double DEFAULT_AVG_EMIT_RATE = 1.0;
	private transient static final int DEFAULT_PARALLELISM = 1;

	@SerializedName(value="avg_emit_rate")
	private Double averageEmitRate;
	

	public SpoutClass(org.eclipse.uml2.uml.Class c) {
			super(c);
			this.parallelism = extractParallelism();
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
	protected int extractParallelism() {
		//Classifier umlBoltType=umlBolt.getClassifiers().get(0);
		String parallelism_s = (String)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "StormSpout"), "parallelism");
		int parallelism = parallelism_s != null ? Integer.parseInt(parallelism_s) : DEFAULT_PARALLELISM;
				//(UML2ModelHelper.getStereotype(umlBoltType, "Bolt"), "parallelism");
		return parallelism;
	}

	protected Double extractAverageEmitRate() {
		String avgEmitRate_s = (String) umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "StormSpout"), "avgEmitRate");
		Double avgEmitRate = avgEmitRate_s != null ? Double.parseDouble(avgEmitRate_s) : DEFAULT_AVG_EMIT_RATE;
		return avgEmitRate;
	}

		
}
