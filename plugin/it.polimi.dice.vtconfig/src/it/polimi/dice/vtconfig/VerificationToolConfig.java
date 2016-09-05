/**
 */
package it.polimi.dice.vtconfig;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;


public interface VerificationToolConfig extends EObject {
		
	ZotPlugin getZotPlugin();
	
	void setZotPlugin(ZotPlugin value);

	EMap<String, Float> getVariableAssignments();
	
	EMap<String, Boolean> getMonitoredBolts();

} 
