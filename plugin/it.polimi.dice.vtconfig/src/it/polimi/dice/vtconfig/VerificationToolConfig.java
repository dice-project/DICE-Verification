/**
 */
package it.polimi.dice.vtconfig;

import java.util.Map;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;


public interface VerificationToolConfig extends EObject {
		
	ZotPlugin getZotPlugin();
	
	void setZotPlugin(ZotPlugin value);

	EMap<String, Float> getVariableAssignments();
	
	EMap<String, Boolean> getMonitoredBolts();

//	Map.Entry<String, Float> getInitialMarking(); ###DELETE

	//void setInitialMarking(Map.Entry<String, Float> value);
	//###DELETE

} 
