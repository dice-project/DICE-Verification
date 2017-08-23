package it.polimi.dice.verification.spark;

public enum Transformation {
	MAP ("map", PartitionEffect.CLEARING),
	FILTER ("filter", PartitionEffect.PRESERVING),
	FLATMAP ("flatMap", PartitionEffect.CLEARING),
	MAPPARTITIONS ("mapPartitions", PartitionEffect.CLEARING), // not sure
	MAPPARTITIONSWITHINDEX ("mapPartitionsWithIndex", PartitionEffect.CLEARING), // not sure
	SAMPLE ("sample", PartitionEffect.PRESERVING), // not sure
	UNION ("union", PartitionEffect.CLEARING),
	INTERSECTION ("intersection", PartitionEffect.CLEARING),
	DISTINCT ("distinct", PartitionEffect.CLEARING),
	BYKEY ("ByKey", PartitionEffect.DEPENDING_ON_PARENT),
	GROUPBYKEY ("groupByKey", PartitionEffect.DEPENDING_ON_PARENT),
	REDUCEBYKEY ("reduceByKey", PartitionEffect.DEPENDING_ON_PARENT),
	AGGREGATEBYKEY ("aggregateByKey", PartitionEffect.DEPENDING_ON_PARENT),
	SORTBYKEY ("sortByKey", PartitionEffect.CLEARING),
	JOIN ("join", PartitionEffect.DEPENDING_ON_PARENT),
	COGROUP ("cogroup", PartitionEffect.DEPENDING_ON_PARENT),
	CARTESIAN ("cartesian", PartitionEffect.CLEARING),
	PIPE ("pipe", PartitionEffect.PRESERVING),
	COALESCE ("coalesce", PartitionEffect.SETTING_NEW),
	REPARTITION ("repartition", PartitionEffect.SETTING_NEW),
	REPARTITIONANDSORTWITHINPARTITIONS ("repartitionAndSortWithPartitions", PartitionEffect.SETTING_NEW), 
	SUBSTRACT ("subtract", PartitionEffect.DEPENDING_ON_PARENT) ,
	PARALLELIZE ("parallelize", PartitionEffect.PRESERVING),
	COMBINEBYKEY ("combineByKey", PartitionEffect.DEPENDING_ON_PARENT),
	MAPTOPAIR ("mapToPair", PartitionEffect.CLEARING),
	TEXTFILE ("textFile", PartitionEffect.PRESERVING);
	
	
	private String name;
	private PartitionEffect partitionEffect;
	private Transformation(String name, PartitionEffect partitionEffect) {
		this.name = name;
		this.partitionEffect = partitionEffect;
	}
	public String getName() {
		return name;
	}
	public PartitionEffect getPartitionEffect() {
		return partitionEffect;
	} 
	
	public static Transformation getEnumFromName(String name){
		if (name != null)
			return valueOf(name.toUpperCase());
		else return MAP;
	}
	
	
	
	
	
}
