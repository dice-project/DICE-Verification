package it.polimi.dice.verification.uml.diagrams.classdiagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;

import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;
import it.polimi.dice.verification.uml2json.Uml2JsonPlugin;

import com.google.gson.annotations.SerializedName;

public class BoltClass extends NodeClass{

//	private org.eclipse.uml2.uml.Class umlClass;
//	private String id;
//	private int parallelism;
	private double alpha;
	private double sigma;
	private transient List<NodeClass> nodeSubscriptionList;
	@SerializedName(value="subs")
	private List<String> stringSubscriptionList;
	@SerializedName(value="min_ttf")
	private int minimumTimeToFailure;
	@SerializedName(value="d")
	private double aggregateCoefficient;
	
/*	public BoltClass(String id, double alpha, double sigma, int parallelism, int minimumTimeToFailure) {
		//super(id, parallelism);
//		this.id = id;
//		this.parallelism = parallelism;
		this.alpha = alpha;
		this.sigma = sigma;
		this.minimumTimeToFailure = minimumTimeToFailure;
		this.nodeSubscriptionList = new ArrayList<NodeClass>();
		this.stringSubscriptionList = new ArrayList<String>();
	}
*/
	public List<NodeClass> getNodeSubscriptionList() {
		return nodeSubscriptionList;
	}

	public void setNodeSubscriptionList(List<NodeClass> nodeSubscriptionSet) {
		this.nodeSubscriptionList = nodeSubscriptionSet;
	}
	public void setNodeSubscriptions(List<NodeClass> nodeSubscriptionSet) {
		this.nodeSubscriptionList = nodeSubscriptionSet;
		for (NodeClass node : nodeSubscriptionSet) {
			this.stringSubscriptionList.add(node.getId());
		}
	}
	
	public List<String> getStringSubscriptionSet() {
		return stringSubscriptionList;
	}

	public void setStringSubscriptionSet(List<String> stringSubscriptionSet) {
		this.stringSubscriptionList = stringSubscriptionSet;
	}

	public BoltClass(org.eclipse.uml2.uml.Class c) {
			super(c);
			this.alpha = extractAlpha();
			this.sigma = extractSigma();
			this.parallelism = extractParallelism();
			this.stringSubscriptionList = extractSubsciptionList();
			this.aggregateCoefficient = 0;
	}

	public String getName() {
		return super.getUmlClass().getName();
	}
	
	
	/** Do not use if you do not know what are you doing **/
	public Class getUMLBolt(){
		return super.getUmlClass();
	}


	@Override
	protected int extractParallelism() {
		//Classifier umlBoltType=umlBolt.getClassifiers().get(0);
		String parallelism_s = (String)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "StormBolt"), "parallelism");
		int parallelism= Integer.parseInt(parallelism_s);
		return parallelism;
	}

	protected Double extractAlpha() {
		String alpha_s = (String)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "StormBolt"), "alpha");
		Double alpha = Double.parseDouble(alpha_s);
		return alpha;
	}

	protected Double extractSigma() {
		String sigma_s = (String)umlClass.getValue(UML2ModelHelper.getStereotype(umlClass, "StormBolt"), "sigma");
		Double sigma = Double.parseDouble(sigma_s);
		return sigma;
	}
	
	protected List<String> extractSubsciptionList(){
		List<String> subsList = new ArrayList<String>();
		for (Association association : this.umlClass.getAssociations()) {
			for(Property property : association.getMemberEnds()){
				if (property.getOwner() != association && !property.getName().toLowerCase().equals(this.umlClass.getName().toLowerCase())) {
					subsList.add(property.getName());
					//DiceLogger.logError(Uml2JsonPlugin.getDefault(), "Adding " + property.getName() + " to subsList of: " + this.umlClass.getName());
				}
			}	
		}
		return subsList;
	}
	
	@Override
	   public String toString() {
	      return "Bolt [id=" + id + ", parallelism=" + parallelism + ", alpha=" + alpha + ", sigma="+ sigma + "]";
	   }
	
}
