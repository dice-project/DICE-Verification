/**
 * 
 */
package it.polimi.dice.vtconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * @author Francesco Marconi
 *
 */
public enum ZotPlugin implements Enumerator {
	
	AE2ZOT(0, "ae2zot", "ae2zot"),
	
	AE2BVZOT(1, "ae2bvzot", "ae2bvzot"),
	
	AE2SBVZOT(2, "ae2sbvzot", "ae2sbvzot");
	
	public static final int AE2ZOT_VALUE = 0;
	
	public static final int AE2BVZOT_VALUE = 1;
	
	public static final int AE2SBVZOT_VALUE = 2;

	
	private static final ZotPlugin[] VALUES_ARRAY =
			new ZotPlugin[] {
				AE2ZOT,
				AE2BVZOT,
				AE2SBVZOT
			};

	public static final List<ZotPlugin> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
	
	public static ZotPlugin get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ZotPlugin result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	public static ZotPlugin getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ZotPlugin result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}
	
	
	public static ZotPlugin get(int value) {
		switch (value) {
			case AE2SBVZOT_VALUE: return ZotPlugin.AE2SBVZOT;
			case AE2BVZOT_VALUE: return ZotPlugin.AE2BVZOT;
			case AE2ZOT_VALUE: return ZotPlugin.AE2ZOT;
		}
		return null;
	}


		
	private int value;
	
	private String literal;
	
	private String name;
		
	
	/**
	 * @param literal
	 * @param name
	 * @param value
	 */
	private ZotPlugin( int value, String literal, String name) {
		this.value = value;
		this.literal = literal;
		this.name = name;
	}

	@Override
	public String getLiteral() {
		return this.literal;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getValue() {
		return this.value;
	}

}
