package it.polimi.dice.verification;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class DiceVerificationPlugin extends Plugin {
	
	public static final String PLUGIN_ID = "it.polimi.dice.verification"; //$NON-NLS-1$

	public static final String VERIFICATION_LAUNCH_CONFIGURATION_TYPE = "it.polimi.dice.verification.launchConfigurationType1"; //$NON-NLS-1$

	private static DiceVerificationPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public DiceVerificationPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DiceVerificationPlugin getDefault() {
		return plugin;
	}


	
}
