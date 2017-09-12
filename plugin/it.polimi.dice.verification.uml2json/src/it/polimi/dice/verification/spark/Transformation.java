package it.polimi.dice.verification.spark;

/**
 * here are all the operations that result in a partitioner being set on the output RDD:
 * cogroup(), x
 * groupWith(), x
 * join(), x
 * leftOuterJoin(), x
 * rightOuterJoin(), x
 * groupByKey(), x
 * reduceByKey(), x
 * combineByKey(), x
 * partitionBy(), x
 * sort(), x
 * mapValues() (if the parent RDD has a partitioner), 
 * flatMapValues() (if parent has apartitioner), 
 * and filter() (if parent has a partitioner).
 * @author francesco
 *
 */

public enum Transformation {
	AGGREGATEBYKEY ("aggregateByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	BYKEY ("ByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	CARTESIAN ("cartesian", PartitionEffect.CLEARING, ShuffleEffect.SHUFFLE),
	COALESCE ("coalesce", PartitionEffect.SETTING_NEW, ShuffleEffect.DEPENDS_ON_PARENTS), // check
	COGROUP ("cogroup", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	COMBINEBYKEY ("combineByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	DISTINCT ("distinct", PartitionEffect.CLEARING, ShuffleEffect.SHUFFLE),
	FILTER ("filter", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	FLATMAP ("flatMap", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE),
	FLATMAPVALUES ("flatMapValues", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	GROUPBYKEY ("groupByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	GROUPWITH ("groupWith", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	INTERSECTION ("intersection", PartitionEffect.CLEARING, ShuffleEffect.SHUFFLE), // check
	JOIN ("join", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	MAP ("map", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE),
	MAPVALUES("mapValues", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	MAPPARTITIONS ("mapPartitions", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE), // not sure
	MAPPARTITIONSWITHINDEX ("mapPartitionsWithIndex", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE), // not sure
	MAPTOPAIR ("mapToPair", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE),
	PARALLELIZE ("parallelize", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	PARITIONBY ("partitionBy", PartitionEffect.SETTING_NEW, ShuffleEffect.DEPENDS_ON_PARENTS),
//	PIPE ("pipe", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	REDUCEBYKEY ("reduceByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	REPARTITION ("repartition", PartitionEffect.SETTING_NEW, ShuffleEffect.SHUFFLE),
	REPARTITIONANDSORTWITHINPARTITIONS ("repartitionAndSortWithPartitions", PartitionEffect.SETTING_NEW, ShuffleEffect.SHUFFLE),
	SAMPLE ("sample", PartitionEffect.PRESERVING, ShuffleEffect.SHUFFLE), // not sure
	SORTBYKEY ("sortByKey", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS),
	SUBTRACT ("subtract", PartitionEffect.DEPENDING_ON_PARENT, ShuffleEffect.DEPENDS_ON_PARENTS) ,
	TEXTFILE ("textFile", PartitionEffect.PRESERVING, ShuffleEffect.NO_SHUFFLE),
	UNION ("union", PartitionEffect.CLEARING, ShuffleEffect.NO_SHUFFLE);
	
	
	private String name;
	private PartitionEffect partitionEffect;
	private ShuffleEffect shuffleEffect;
	
	private Transformation(String name, PartitionEffect partitionEffect, ShuffleEffect shuffleEffect) {
		this.name = name;
		this.partitionEffect = partitionEffect;
		this.shuffleEffect = shuffleEffect;
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
	
	public ShuffleEffect getShuffleEffect() {
		return shuffleEffect;
	}
	
	
	
}
