/**
 */
package it.polimi.dice.vtconfig.util;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import it.polimi.dice.vtconfig.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see it.polimi.dice.vtconfig.VtConfigPackage
 * @generated
 */
public class VtconfigSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static VtConfigPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VtconfigSwitch() {
		if (modelPackage == null) {
			modelPackage = VtConfigPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case VtConfigPackage.PETRI_NET_CONFIG: {
				VerificationToolConfig verificationToolConfig = (VerificationToolConfig)theEObject;
				T result = caseVerificationToolConfig(verificationToolConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case VtConfigPackage.ESTRING_TO_EFLOAT_OBJECTS_MAP: {
				@SuppressWarnings("unchecked") Map.Entry<String, Float> eStringToEFloatObjectsMap = (Map.Entry<String, Float>)theEObject;
				T result = caseEStringToEFloatObjectsMap(eStringToEFloatObjectsMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case VtConfigPackage.ESTRING_TO_EBOOLEAN_OBJECTS_MAP: {
				@SuppressWarnings("unchecked") Map.Entry<String, Boolean> eStringToEBooleanObjectsMap = (Map.Entry<String, Boolean>)theEObject;
				T result = caseEStringToEBooleanObjectsMap(eStringToEBooleanObjectsMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}

			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Petri Net Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Petri Net Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVerificationToolConfig(VerificationToolConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EString To EFloat Objects Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EString To EFloat Objects Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEStringToEFloatObjectsMap(Map.Entry<String, Float> object) {
		return null;
	}

	public T caseEStringToEBooleanObjectsMap(Map.Entry<String, Boolean> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //PnconfigSwitch
