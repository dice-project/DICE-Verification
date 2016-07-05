/**
 */
package it.polimi.dice.vtconfig;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see it.polimi.dice.vtconfig.VtConfigFactory
 * @model kind="package"
 * @generated
 */
public interface VtConfigPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pnconfig";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://it.polimi.dice/vtconfig/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "vtconfig";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	VtConfigPackage eINSTANCE = it.polimi.dice.vtconfig.impl.VtConfigPackageImpl.init();

	/**
	 * The meta object id for the '{@link it.polimi.dice.vtconfig.impl.VerificationToolConfigImpl <em>Petri Net Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.polimi.dice.vtconfig.impl.VerificationToolConfigImpl
	 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getPetriNetConfig()
	 * @generated
	 */
	int PETRI_NET_CONFIG = 0;
	int VERIFICATION_TOOL_CONFIG = 0;

	/**
	 * The feature id for the '<em><b>Analysis Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN = 0;

	/**
	 * The feature id for the '<em><b>Variable Assignments</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS = 1;
	
	int VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS = 21;

	/**
	 * The feature id for the '<em><b>Initial Marking</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PETRI_NET_CONFIG__INITIAL_MARKING = 2;

	/**
	 * The number of structural features of the '<em>Petri Net Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PETRI_NET_CONFIG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Petri Net Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PETRI_NET_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link it.polimi.dice.vtconfig.impl.EStringToEFloatObjectsMapImpl <em>EString To EFloat Objects Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.polimi.dice.vtconfig.impl.EStringToEFloatObjectsMapImpl
	 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getEStringToEFloatObjectsMap()
	 * @generated
	 */
	int ESTRING_TO_EFLOAT_OBJECTS_MAP = 1;
	
	
	int ESTRING_TO_EBOOLEAN_OBJECTS_MAP = 21;
	

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESTRING_TO_EFLOAT_OBJECTS_MAP__KEY = 0;

	int ESTRING_TO_EBOOLEAN_OBJECTS_MAP__KEY = 20;
	
	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESTRING_TO_EFLOAT_OBJECTS_MAP__VALUE = 1;
	
	int ESTRING_TO_EBOOLEAN_OBJECTS_MAP__VALUE = 21;

	/**
	 * The number of structural features of the '<em>EString To EFloat Objects Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESTRING_TO_EFLOAT_OBJECTS_MAP_FEATURE_COUNT = 2;
	
	int ESTRING_TO_EBOOLEAN_OBJECTS_MAP_FEATURE_COUNT = 22;

	/**
	 * The number of operations of the '<em>EString To EFloat Objects Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESTRING_TO_EFLOAT_OBJECTS_MAP_OPERATION_COUNT = 0;

	int ESTRING_TO_EBOOLEAN_OBJECTS_MAP_OPERATION_COUNT = 20;
	
	/**
	 * The meta object id for the '{@link it.polimi.dice.vtconfig.AnalysisType <em>Analysis Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see it.polimi.dice.vtconfig.AnalysisType
	 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getAnalysisType()
	 * @generated
	 */
	int ZOT_PLUGIN = 2;


	/**
	 * Returns the meta object for class '{@link it.polimi.dice.vtconfig.VerificationToolConfig <em>Petri Net Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Petri Net Config</em>'.
	 * @see it.polimi.dice.vtconfig.VerificationToolConfig
	 * @generated
	 */
	EClass getPetriNetConfig();
	
	EClass getVerificationToolConfig();

	/**
	 * Returns the meta object for the attribute '{@link it.polimi.dice.vtconfig.VerificationToolConfig#getAnalysisType <em>Analysis Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Analysis Type</em>'.
	 * @see it.polimi.dice.vtconfig.VerificationToolConfig#getAnalysisType()
	 * @see #getPetriNetConfig()
	 * @generated
	 */
	EAttribute getPetriNetConfig_AnalysisType();
	
	EAttribute getVerificationToolConfig_ZotPlugin();

	/**
	 * Returns the meta object for the map '{@link it.polimi.dice.vtconfig.VerificationToolConfig#getVariableAssignments <em>Variable Assignments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Variable Assignments</em>'.
	 * @see it.polimi.dice.vtconfig.VerificationToolConfig#getVariableAssignments()
	 * @see #getPetriNetConfig()
	 * @generated
	 */
	EReference getPetriNetConfig_VariableAssignments();
	
	
	EReference getVerificationToolConfig_MonitoredBolts();

	/**
	 * Returns the meta object for the reference '{@link it.polimi.dice.vtconfig.VerificationToolConfig#getInitialMarking <em>Initial Marking</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Initial Marking</em>'.
	 * @see it.polimi.dice.vtconfig.VerificationToolConfig#getInitialMarking()
	 * @see #getPetriNetConfig()
	 * @generated
	 */
	EReference getPetriNetConfig_InitialMarking();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>EString To EFloat Objects Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EString To EFloat Objects Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString" keyRequired="true"
	 *        valueDataType="org.eclipse.emf.ecore.EFloatObject" valueRequired="true"
	 * @generated
	 */
	EClass getEStringToEFloatObjectsMap();
	
	EClass getEStringToEBooleanObjectsMap();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEStringToEFloatObjectsMap()
	 * @generated
	 */
	EAttribute getEStringToEFloatObjectsMap_Key();
	
	EAttribute getEStringToEBooleanObjectsMap_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEStringToEFloatObjectsMap()
	 * @generated
	 */
	EAttribute getEStringToEFloatObjectsMap_Value();
	
	EAttribute getEStringToEBooleanObjectsMap_Value();
	/**
	 * Returns the meta object for enum '{@link it.polimi.dice.vtconfig.AnalysisType <em>Analysis Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Analysis Type</em>'.
	 * @see it.polimi.dice.vtconfig.AnalysisType
	 * @generated
	 */
	EEnum getAnalysisType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	VtConfigFactory getPnconfigFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link it.polimi.dice.vtconfig.impl.VerificationToolConfigImpl <em>Petri Net Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.polimi.dice.vtconfig.impl.VerificationToolConfigImpl
		 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getPetriNetConfig()
		 * @generated
		 */
		EClass PETRI_NET_CONFIG = eINSTANCE.getPetriNetConfig();
		
		EClass VERIFICATION_TOOL_CONFIG = eINSTANCE.getVerificationToolConfig();

		/**
		 * The meta object literal for the '<em><b>Analysis Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PETRI_NET_CONFIG__ANALYSIS_TYPE = eINSTANCE.getPetriNetConfig_AnalysisType();

		/**
		 * The meta object literal for the '<em><b>Variable Assignments</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS = eINSTANCE.getPetriNetConfig_VariableAssignments();
		
		EReference VERIFICATION_TOOL__MONITORED_BOLTS = eINSTANCE.getVerificationToolConfig_MonitoredBolts();
		
		/**
		 * The meta object literal for the '<em><b>Initial Marking</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PETRI_NET_CONFIG__INITIAL_MARKING = eINSTANCE.getPetriNetConfig_InitialMarking();

		/**
		 * The meta object literal for the '{@link it.polimi.dice.vtconfig.impl.EStringToEFloatObjectsMapImpl <em>EString To EFloat Objects Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.polimi.dice.vtconfig.impl.EStringToEFloatObjectsMapImpl
		 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getEStringToEFloatObjectsMap()
		 * @generated
		 */
		EClass ESTRING_TO_EFLOAT_OBJECTS_MAP = eINSTANCE.getEStringToEFloatObjectsMap();
		
		EClass ESTRING_TO_EBOOLEAN_OBJECTS_MAP = eINSTANCE.getEStringToEBooleanObjectsMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESTRING_TO_EFLOAT_OBJECTS_MAP__KEY = eINSTANCE.getEStringToEFloatObjectsMap_Key();
		
		EAttribute ESTRING_TO_EBOOLEAN_OBJECTS_MAP__KEY = eINSTANCE.getEStringToEBooleanObjectsMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESTRING_TO_EFLOAT_OBJECTS_MAP__VALUE = eINSTANCE.getEStringToEFloatObjectsMap_Value();

		EAttribute ESTRING_TO_EBOOLEAN_OBJECTS_MAP__VALUE = eINSTANCE.getEStringToEBooleanObjectsMap_Value();
		
		/**
		 * The meta object literal for the '{@link it.polimi.dice.vtconfig.AnalysisType <em>Analysis Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see it.polimi.dice.vtconfig.AnalysisType
		 * @see it.polimi.dice.vtconfig.impl.VtConfigPackageImpl#getAnalysisType()
		 * @generated
		 */
		EEnum ANALYSIS_TYPE = eINSTANCE.getAnalysisType();

	}

} //PnconfigPackage
