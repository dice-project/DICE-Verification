package it.polimi.dice.verification.launcher;

public interface VerificationLaunchConfigurationAttributes {
	
		public static final String INPUT_FILE = "it.polimi.dice.verification.launcher.console.inputModel"; //"INPUT_MODEL"; //$NON-NLS-1$
		public static final String KEEP_INTERMEDIATE_FILES= "it.polimi.dice.verification.launcher.console.keepIntermediateFiles"; //"KEEP_INTERMEDIATE_FILES"; //$NON-NLS-1$
		public static final String INTERMEDIATE_FILES_DIR = "it.polimi.dice.verification.launcher.console.intermediateFilesFolder"; 
		public static final String TIME_BOUND = "it.polimi.dice.verification.launcher.console.timeBound"; //"TIME BOUND"; //$NON-NLS-1$
		public static final String VERIFICATION_CONFIGURATION = "it.polimi.dice.verification.launcher.console.verificationConfiguration"; //"VERIFICATION_CONFIGURATION"; //$NON-NLS-1$
		public static final String HOST_ADDRESS = "it.polimi.dice.verification.launcher.console.hostAddress";
		public static final String PORT_NUMBER = "it.polimi.dice.verification.launcher.console.portNumber";
	 
		public static final String SPARK_ANALYSIS_TYPE = "it.polimi.dice.verification.launcher.console.spark.analyisisType";
		public static final String SPARK_DEADLINE = "it.polimi.dice.verification.launcher.console.spark.deadline";
		
		public static final String TASK_NAME = "it.polimi.dice.verification.launcher.console.taskName";
		public static final String TASK_DESCRIPTION = "it.polimi.dice.verification.launcher.console.taskDescription";
	} 
