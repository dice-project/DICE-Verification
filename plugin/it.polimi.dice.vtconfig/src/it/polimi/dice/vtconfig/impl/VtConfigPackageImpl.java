/**
 */
package it.polimi.dice.vtconfig.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;


import it.polimi.dice.vtconfig.VerificationToolConfig;
import it.polimi.dice.vtconfig.VtConfigFactory;
import it.polimi.dice.vtconfig.VtConfigPackage;
import it.polimi.dice.vtconfig.ZotPlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class VtConfigPackageImpl extends EPackageImpl implements VtConfigPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass petriNetConfigEClass = null;
	
	private EClass verificationToolConFigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eStringToEFloatObjectsMapEClass = null;
	
	private EClass eStringToEBooleanObjectsMapEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum zotPluginEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see it.polimi.dice.vtconfig.VtConfigPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private VtConfigPackageImpl() {
		super(eNS_URI, VtConfigFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link VtConfigPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static VtConfigPackage init() {
		if (isInited) return (VtConfigPackage)EPackage.Registry.INSTANCE.getEPackage(VtConfigPackage.eNS_URI);

		// Obtain or create and register package
		VtConfigPackageImpl theVtConfigPackage = (VtConfigPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof VtConfigPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new VtConfigPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theVtConfigPackage.createPackageContents();

		// Initialize created meta-data
		theVtConfigPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theVtConfigPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(VtConfigPackage.eNS_URI, theVtConfigPackage);
		return theVtConfigPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPetriNetConfig() {
		return petriNetConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPetriNetConfig_AnalysisType() {
		return (EAttribute)petriNetConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPetriNetConfig_VariableAssignments() {
		return (EReference)petriNetConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPetriNetConfig_InitialMarking() {
		return (EReference)petriNetConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEStringToEFloatObjectsMap() {
		return eStringToEFloatObjectsMapEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEStringToEFloatObjectsMap_Key() {
		return (EAttribute)eStringToEFloatObjectsMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEStringToEFloatObjectsMap_Value() {
		return (EAttribute)eStringToEFloatObjectsMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getAnalysisType() {
		return zotPluginEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VtConfigFactory getPnconfigFactory() {
		return (VtConfigFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		petriNetConfigEClass = createEClass(PETRI_NET_CONFIG);
		createEAttribute(petriNetConfigEClass, VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN);
		createEReference(petriNetConfigEClass, PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS);
		createEReference(petriNetConfigEClass, PETRI_NET_CONFIG__INITIAL_MARKING);

		
		verificationToolConFigEClass = createEClass(VERIFICATION_TOOL_CONFIG);
		createEAttribute(verificationToolConFigEClass, VERIFICATION_TOOL_CONFIG__ZOT_PLUGIN);
		createEReference(verificationToolConFigEClass, PETRI_NET_CONFIG__VARIABLE_ASSIGNMENTS);
		createEReference(verificationToolConFigEClass, VERIFICATION_TOOL_CONFIG__MONITORED_BOLTS);
		//createEReference(verificationToolConFigEClass, PETRI_NET_CONFIG__INITIAL_MARKING);

		
		
		eStringToEFloatObjectsMapEClass = createEClass(ESTRING_TO_EFLOAT_OBJECTS_MAP);
		createEAttribute(eStringToEFloatObjectsMapEClass, ESTRING_TO_EFLOAT_OBJECTS_MAP__KEY);
		createEAttribute(eStringToEFloatObjectsMapEClass, ESTRING_TO_EFLOAT_OBJECTS_MAP__VALUE);
		
		eStringToEBooleanObjectsMapEClass = createEClass(ESTRING_TO_EBOOLEAN_OBJECTS_MAP);
		createEAttribute(eStringToEBooleanObjectsMapEClass, ESTRING_TO_EBOOLEAN_OBJECTS_MAP__KEY);
		createEAttribute(eStringToEBooleanObjectsMapEClass, ESTRING_TO_EBOOLEAN_OBJECTS_MAP__VALUE);

		// Create enums
		zotPluginEEnum = createEEnum(ZOT_PLUGIN);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(petriNetConfigEClass, VerificationToolConfig.class, "PetriNetConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPetriNetConfig_AnalysisType(), this.getAnalysisType(), "analysisType", null, 1, 1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPetriNetConfig_VariableAssignments(), this.getEStringToEFloatObjectsMap(), null, "variableAssignments", null, 0, -1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPetriNetConfig_InitialMarking(), this.getEStringToEFloatObjectsMap(), null, "initialMarking", null, 1, 1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize classes, features, and operations; add parameters
				initEClass(verificationToolConFigEClass, VerificationToolConfig.class, "VerificationToolConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
				initEAttribute(getVerificationToolConfig_ZotPlugin(), this.getAnalysisType(), "zotPlugin", null, 1, 1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
				initEReference(getVerificationToolConfig_MonitoredBolts(), this.getEStringToEBooleanObjectsMap(), null, "monitoredBolts", null, 0, -1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		//		initEReference(getPetriNetConfig_InitialMarking(), this.getEStringToEFloatObjectsMap(), null, "initialMarking", null, 1, 1, VerificationToolConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		
		
		initEClass(eStringToEFloatObjectsMapEClass, Map.Entry.class, "EStringToEFloatObjectsMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEStringToEFloatObjectsMap_Key(), ecorePackage.getEString(), "key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEStringToEFloatObjectsMap_Value(), ecorePackage.getEFloatObject(), "value", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eStringToEBooleanObjectsMapEClass, Map.Entry.class, "EStringToEBooleanObjectsMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEStringToEBooleanObjectsMap_Key(), ecorePackage.getEString(), "key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEStringToEBooleanObjectsMap_Value(), ecorePackage.getEBooleanObject(), "value", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		
		// Initialize enums and add enum literals
		initEEnum(zotPluginEEnum, ZotPlugin.class, "ZotPlugin");
		addEEnumLiteral(zotPluginEEnum, ZotPlugin.AE2SBVZOT);
		addEEnumLiteral(zotPluginEEnum, ZotPlugin.AE2BVZOT);
		addEEnumLiteral(zotPluginEEnum, ZotPlugin.AE2ZOT);

		// Create resource
		createResource(eNS_URI);
	}


	@Override
	public EClass getEStringToEBooleanObjectsMap() {
		return eStringToEBooleanObjectsMapEClass;
	}

	@Override
	public EAttribute getEStringToEBooleanObjectsMap_Key() {
		return (EAttribute)eStringToEBooleanObjectsMapEClass.getEStructuralFeatures().get(0);
	}

	@Override
	public EAttribute getEStringToEBooleanObjectsMap_Value() {
		return (EAttribute)eStringToEBooleanObjectsMapEClass.getEStructuralFeatures().get(1);
	}

	@Override
	public EClass getVerificationToolConfig() {
		return verificationToolConFigEClass;
	}

	@Override
	public EAttribute getVerificationToolConfig_ZotPlugin() {
		return (EAttribute)verificationToolConFigEClass.getEStructuralFeatures().get(0);
	}

	@Override
	public EReference getVerificationToolConfig_MonitoredBolts() {
		return (EReference)verificationToolConFigEClass.getEStructuralFeatures().get(1);
	}

} //VtConfigPackageImpl
