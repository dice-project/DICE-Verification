/**
 */
package it.polimi.dice.vtconfig.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import it.polimi.dice.vtconfig.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class VtConfigFactoryImpl extends EFactoryImpl implements VtConfigFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static VtConfigFactory init() {
		try {
			VtConfigFactory theVtconfigFactory = (VtConfigFactory)EPackage.Registry.INSTANCE.getEFactory(VtConfigPackage.eNS_URI);
			if (theVtconfigFactory != null) {
				return theVtconfigFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new VtConfigFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VtConfigFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	/*@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case VtConfigPackage.PETRI_NET_CONFIG: return createPetriNetConfig();
			case VtConfigPackage.ESTRING_TO_EFLOAT_OBJECTS_MAP: return (EObject)createEStringToEFloatObjectsMap();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}*/

	//###
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case VtConfigPackage.VERIFICATION_TOOL_CONFIG: return createVerificationToolConfig();
			case VtConfigPackage.ESTRING_TO_EFLOAT_OBJECTS_MAP: return (EObject)createEStringToEFloatObjectsMap();
			case VtConfigPackage.ESTRING_TO_EBOOLEAN_OBJECTS_MAP: return (EObject)createEStringToEBooleanObjectsMap();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case VtConfigPackage.ZOT_PLUGIN:
				return createZotPluginFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case VtConfigPackage.ZOT_PLUGIN:
				return convertZotPluginToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VerificationToolConfig createVerificationToolConfig() {
		VerificationToolConfigImpl verificationToolConfig = new VerificationToolConfigImpl();
		return verificationToolConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Float> createEStringToEFloatObjectsMap() {
		EStringToEFloatObjectsMapImpl eStringToEFloatObjectsMap = new EStringToEFloatObjectsMapImpl();
		return eStringToEFloatObjectsMap;
	}


	public Map.Entry<String, Boolean> createEStringToEBooleanObjectsMap() {
		EStringToEBooleanObjectsMapImpl eStringToEBooleanObjectsMap = new EStringToEBooleanObjectsMapImpl();
		return eStringToEBooleanObjectsMap;
	}

	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ZotPlugin createZotPluginFromString(EDataType eDataType, String initialValue) {
		ZotPlugin result = ZotPlugin.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertZotPluginToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VtConfigPackage getVtConfigPackage() {
		return (VtConfigPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static VtConfigPackage getPackage() {
		return VtConfigPackage.eINSTANCE;
	}

} //PnconfigFactoryImpl
