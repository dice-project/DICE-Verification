package it.polimi.dice.verification.launcher;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.dice.verification.json.SparkStage;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkOperatorDAG;
import it.polimi.dice.verification.uml2json.transformations.SparkTransformations;

public class SparkVerificationLaunchConfigurationDelegate extends LaunchConfigurationDelegate{

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
	
		String serverAddress = configuration.getAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS, "http://localhost");
		String serverPort = configuration.getAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER, "5000");
		int timeBound = configuration.getAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 20);
		int deadline = configuration.getAttribute(VerificationLaunchConfigurationAttributes.SPARK_DEADLINE, 400);
		String taskName = configuration.getAttribute(VerificationLaunchConfigurationAttributes.TASK_NAME, "");
		String inputFilePath = configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY);
		boolean keepIntermediateFiles = configuration.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false);
		String intermediateFilesDir = configuration.getAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, "");
		String description = configuration.getAttribute(VerificationLaunchConfigurationAttributes.TASK_DESCRIPTION, "");
		
		
		System.out.println("Server Address: " + serverAddress);
		System.out.println("Server Port: " + serverPort);
		System.out.println("Time Bound: " + timeBound);
		System.out.println("Deadline: " + deadline);
		System.out.println("Task Name: "+ taskName);
		System.out.println("Input file: " + inputFilePath);
		System.out.println("Keep Intermediate files: " + keepIntermediateFiles);
		System.out.println("Intermediate FIles Dir: " + intermediateFilesDir);
		System.out.println("Description: " + description);
		
		
		File umlFile = Utils.getInputFile(configuration);
		
		SparkOperatorDAG operatorDAG = SparkTransformations.getOperationsDAGFromUmlFile(umlFile);
		
		Map<Integer, SparkStage> executionDAG = SparkTransformations.getExecutionDAGFromOperatorDAG(operatorDAG);
		
		Gson gsonBuilder = new GsonBuilder().create();
		
		System.out.println(gsonBuilder.toJson(executionDAG));
		
		
		
	}

	

}
