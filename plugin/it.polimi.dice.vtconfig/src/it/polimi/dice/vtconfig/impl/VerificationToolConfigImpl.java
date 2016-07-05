/**
 */
package it.polimi.dice.vtconfig.impl;

import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import it.polimi.dice.vtconfig.VerificationToolConfig;
import it.polimi.dice.vtconfig.VtConfigPackage;
import it.polimi.dice.vtconfig.ZotPlugin;


public class VerificationToolConfigImpl extends MinimalEObjectImpl.Container implements VerificationToolConfig {
	/**
	 * The default value of the '{@link #getAnalysisType() <em>Analysis Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnalysisType()
	 * @generated
	 * @ordered
	 */
	protected static final ZotPlugin ZOT_PLUGIN_EDEFAULT = ZotPlugin.AE2SBVZOT;

	/**
	 * The cached value of the '{@link #getAnalysisType() <em>Analysis Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnalysisType()
	 * @generated
	 * @ordered
	 */
	protected ZotPlugin zotPlugin = ZOT_PLUGIN_EDEFAULT;

	/**
	 * The cached value of the '{@link #getVariableAssignments() <em>Variable Assignments</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariableAssignments()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Float> variableAssignments;
	
	
	protected EMap<String, Boolean> monitoredBolts;

	/**
	 * The cached value of the '{@link #getInitialMarking() <em>Initial Marking</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialMarking()
	 * @generated
	 * @ordered
	 */
	protected Map.Entry<String, Float> initialMarking;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VerificationToolConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return VtConfigPackage.Literals.VERIFICATION_TOOL_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ZotPlugin getZotPlugin() {
		return zotPlugin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setZotPlugin(ZotPlugin newZotPlugin) {
		ZotPlugin oldZotPlugin = zotPlugin;
		zotPlugin = newZotPlugin == null ? ZOT_PLUGIN_EDEFAULT : newZotPlugin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, VtConfigPackage.VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN, oldZotPlugin, zotPlugin));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Float> getVariableAssignments() {
		if (variableAssignments == null) {
			variableAssignments = new EcoreEMap<String,Float>(VtConfigPackage.Literals.ESTRING_TO_EFLOAT_OBJECTS_MAP, EStringToEFloatObjectsMapImpl.class, this, VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS);
		}
		return variableAssignments;
	}

	@Override
	public EMap<String, Boolean> getMonitoredBolts() {
		if (monitoredBolts == null) {
			monitoredBolts = new EcoreEMap<String,Boolean>(VtConfigPackage.Literals.ESTRING_TO_EBOOLEAN_OBJECTS_MAP, EStringToEBooleanObjectsMapImpl.class, this, VtConfigPackage.VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS);
		}
		return monitoredBolts;
	}

	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Map.Entry<String, Float> getInitialMarking() {
		if (initialMarking != null && ((EObject)initialMarking).eIsProxy()) {
			InternalEObject oldInitialMarking = (InternalEObject)initialMarking;
			initialMarking = (Map.Entry<String, Float>)eResolveProxy(oldInitialMarking);
			if (initialMarking != oldInitialMarking) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING, oldInitialMarking, initialMarking));
			}
		}
		return initialMarking;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Float> basicGetInitialMarking() {
		return initialMarking;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitialMarking(Map.Entry<String, Float> newInitialMarking) {
		Map.Entry<String, Float> oldInitialMarking = initialMarking;
		initialMarking = newInitialMarking;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING, oldInitialMarking, initialMarking));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS:
				return ((InternalEList<?>)getVariableAssignments()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN:
				return getZotPlugin();
			case VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS:
				if (coreType) return getVariableAssignments();
				else return getVariableAssignments().map();
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS:
				if (coreType) return getMonitoredBolts();
				else return getMonitoredBolts().map();
			case VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING:
				if (resolve) return getInitialMarking();
				return basicGetInitialMarking();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN:
				setZotPlugin((ZotPlugin) newValue);
				return;
			case VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS:
				((EStructuralFeature.Setting)getVariableAssignments()).set(newValue);
				return;
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS:
				((EStructuralFeature.Setting)getMonitoredBolts()).set(newValue);
				return;
			case VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING:
				setInitialMarking((Map.Entry<String, Float>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN:
				setZotPlugin(ZOT_PLUGIN_EDEFAULT);
				return;
			case VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS:
				getVariableAssignments().clear();
				return;
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS:
				getMonitoredBolts().clear();
				return;
			case VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING:
				setInitialMarking((Map.Entry<String, Float>)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN:
				return zotPlugin != ZOT_PLUGIN_EDEFAULT;
			case VtConfigPackage.PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS:
				return variableAssignments != null && !variableAssignments.isEmpty();
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS:
				return monitoredBolts != null && !monitoredBolts.isEmpty();
			case VtConfigPackage.PETRI_NET_CONFIG__INITIAL_MARKING:
				return initialMarking != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (zotPlugin: ");
		result.append(zotPlugin);
		result.append(')');
		return result.toString();
	}


} 
