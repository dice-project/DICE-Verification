package it.polimi.dice.verification.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import it.polimi.dice.verification.uml.diagrams.classdiagram.BoltClass;

public class StormVerificationParameters extends VerificationParameters {

	@SerializedName(value="base_quantity")
	private int baseQuantity;
	@SerializedName(value="periodic_queues")
	private List<String> periodicQueues;
	@SerializedName(value="strictly_monotonic_queues")
	private List<String> strictlyMonotonicQueues;

	public StormVerificationParameters(){
		super();
		// TODO check if basequantity can be discarded
		this.baseQuantity = 5;
		this.periodicQueues = new ArrayList<>();
		this.strictlyMonotonicQueues = new ArrayList<>();			
	}
	
	public int getBaseQuantity() {
		return baseQuantity;
	}

	public void setBaseQuantity(int baseQuantity) {
		this.baseQuantity = baseQuantity;
	}
		
	public List<String> getPeriodicQueues() {
		return periodicQueues;
	}

	public void setPeriodicQueues(List<String> periodicQueues) {
		this.periodicQueues = periodicQueues;
	}
	
	public void setPeriodicQueuesFromBolts(List<BoltClass> periodicQueues) {
		List<String> result = new ArrayList<>();
		for (BoltClass bolt : periodicQueues) {
			result.add(bolt.getId());
		}
		this.periodicQueues = result;
	}

	public List<String> getStrictlyMonotonicQueues() {
		return strictlyMonotonicQueues;
	}

	public void setStrictlyMonotonicQueues(List<String> strictlyMonotonicQueues) {
		this.strictlyMonotonicQueues = strictlyMonotonicQueues;
	}
}
