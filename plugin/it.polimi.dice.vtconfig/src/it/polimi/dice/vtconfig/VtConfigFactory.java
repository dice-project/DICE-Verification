/**
 */
package it.polimi.dice.vtconfig;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see it.polimi.dice.vtconfig.VtConfigPackage
 * @generated
 */
public interface VtConfigFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	VtConfigFactory eINSTANCE = it.polimi.dice.vtconfig.impl.VtConfigFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Petri Net Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Petri Net Config</em>'.
	 * @generated
	 */
	VerificationToolConfig createVerificationToolConfig();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	VtConfigPackage getVtConfigPackage();

} //VtConfigFactory
