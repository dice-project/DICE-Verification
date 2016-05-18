package it.polimi.dice.verification.launcher;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.polimi.dice.verification.launcher.messages"; //$NON-NLS-1$
	public static String VerificationLaunchConfigurationDelegate_dumpingTaskTitle;
	public static String VerificationLaunchConfigurationDelegate_generatingJsonTaskTitle;
	//public static String VerificationLaunchConfigurationDelegate_generatingPnmlTaskTitle;
	public static String VerificationLaunchConfigurationDelegate_invalidLocationError;
	public static String VerificationLaunchConfigurationDelegate_invalidLocationIntermediateFilesError;
	public static String VerificationLaunchConfigurationDelegate_verificationTaskTitle;
	public static String VerificationLaunchConfigurationDelegate_verificationName;
	public static String VerificationLaunchConfigurationDelegate_verifierNotFoundError;
	public static String VerificationLaunchConfigurationDelegate_unableCreateTempFileError;
	public static String VerificationLaunchConfigurationDelegate_unableDeserializeError;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
