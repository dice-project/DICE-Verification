package it.polimi.dice.verification.spark;

public enum Action {
	REDUCE, COLLECT, COUNT, FIRST, TAKE, TAKESAMPLE, TAKEORDERED, SAVEASTEXTFILE, SAVEASSEQUENCEFILE, SAVEASOBJECTFILE, COUNTBYKEY, FOREACH, COLLECTASMAP, AGGREGATE, FOLD;


	public static Action getEnumFromName(String name){
		if (name != null)
			return valueOf(name.toUpperCase());
		else return REDUCE;
	}


}


