package it.polimi.dice.verification.json;

public class StormVerificationJsonContext extends VerificationJsonContext{
	
	private StormTopology topology;
	
	public StormVerificationJsonContext(StormTopology t, VerificationParameters vp){
		super(vp);
		this.topology = t;
	}

	public StormTopology getTopology() {
		return topology;
	}


	public void setTopology(StormTopology topology) {
		this.topology = topology;
	}

}
