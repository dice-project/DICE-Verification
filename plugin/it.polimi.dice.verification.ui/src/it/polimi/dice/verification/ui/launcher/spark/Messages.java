package it.polimi.dice.verification.ui.launcher.spark;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "it.polimi.dice.verification.ui.launcher.messages"; //$NON-NLS-1$
	public static String MainLaunchConfigurationTab_noSparkProfileApplied;
	public static String MainLaunchConfigurationTab_analysisTypeLabel;
	public static String MainLaunchConfigurationTab_browseLabel;
	public static String MainLaunchConfigurationTab_browsLabel;
	public static String MainLaunchConfigurationTab_errorTitle;
	public static String MainLaunchConfigurationTab_inputNotExistsError;
	public static String MainLaunchConfigurationTab_intermediateDirNotExistError;
	public static String MainLaunchConfigurationTab_intermediateFilesLabel;
	public static String MainLaunchConfigurationTab_timeBoundLabel;
	public static String MainLaunchConfigurationTab_invalidFloatError;
	public static String MainLaunchConfigurationTab_invalidBooleanError;
	public static String MainLaunchConfigurationTab_invalidUmlFileError;
	public static String MainLaunchConfigurationTab_mainTabTitle;
	public static String MainLaunchConfigurationTab_modelLabel;
	public static String MainLaunchConfigurationTab_noDirForIntermediateError;
	public static String MainLaunchConfigurationTab_noInpuntError;
	public static String MainLaunchConfigurationTab_notUml2InputError;
	public static String MainLaunchConfigurationTab_saveIntermediateLabel;
	public static String MainLaunchConfigurationTab_unableParserError;
	public static String MainLaunchConfigurationTab_connectionLabel;
	public static String MainLaunchConfigurationTab_hostAddressLabel;
	public static String MainLaunchConfigurationTab_portNumberLabel;
	public static String VerificationLaunchShortcut_unexpectedArgError;
	public static String VerificationLaunchShortcut_unknownModeError;
	public static String SparkMainLaunchConfigurationTab_grpAnalysisSettings_text;
	public static String SparkMainLaunchConfigurationTab_lblAnalysisType_text;
	public static String SparkMainLaunchConfigurationTab_lblDeadline_text;
	

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
