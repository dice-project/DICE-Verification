package it.polimi.dice.verification.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.verification.DiceVerificationPlugin;
import it.polimi.dice.verification.httpclient.HttpClient;
import it.polimi.dice.verification.json.JsonVerificationTaskRequest;
import it.polimi.dice.verification.json.SparkStage;
import it.polimi.dice.verification.json.SparkVerificationJsonContext;
import it.polimi.dice.verification.json.SparkVerificationParameters;
import it.polimi.dice.verification.json.VerificationJsonContext;
import it.polimi.dice.verification.json.VerificationParameters;
import it.polimi.dice.verification.uml.diagrams.activitydiagram.SparkOperatorDAG;
import it.polimi.dice.verification.uml2json.transformations.SparkTransformations;

public class SparkVerificationLaunchConfigurationDelegate extends LaunchConfigurationDelegate{

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd__HH_mm_ss");
		LocalDateTime now = LocalDateTime.now();
		String now_s = now.format(formatter);
		
		String analysisType = configuration.getAttribute(VerificationLaunchConfigurationAttributes.SPARK_ANALYSIS_TYPE, SparkAnalysisType.FEASIBILITY.toString());
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
		System.out.println("Analysis Type:" + analysisType);
		
		// building URLs
		String dashboardUrl = serverAddress.replaceAll("/$", "") + ":" + serverPort;
		String launchVerificationUrl =  dashboardUrl + "/longtasks";
		DiceLogger.logInfo(DiceVerificationPlugin.getDefault(), "Building url:\n" + launchVerificationUrl);
		
		
		File umlFile = Utils.getInputFile(configuration);
		
		SparkOperatorDAG operatorDAG = SparkTransformations.getOperationsDAGFromUmlFile(umlFile);
		
		Map<Integer, SparkStage> executionDAG = SparkTransformations.getExecutionDAGFromOperatorDAG(operatorDAG);
		
		Gson gsonBuilder = new GsonBuilder().create();
		
		
		// TODO let it be configurable from launchConfig
		VerificationParameters vp = new SparkVerificationParameters(true, true, timeBound);
		VerificationJsonContext jsonContext = new SparkVerificationJsonContext(vp, analysisType, deadline, 0.01, 48, executionDAG);
		
		String filename = umlFile.getName();
		int pos = filename.lastIndexOf(".");
		String justName = pos > 0 ? filename.substring(0, pos) : filename;	
		
		String appName = justName+"_"+now_s;
		jsonContext.setApplicationName(appName);
		
		String requestTitle = taskName + "_" + now_s;
		
		JsonVerificationTaskRequest request = new JsonVerificationTaskRequest(requestTitle, jsonContext, "spark");
		
		
		HttpClient nc = new HttpClient();
		boolean connectionSuccessful;
		connectionSuccessful = nc.postJSONRequest(launchVerificationUrl, gsonBuilder.toJson(request));
		
		if (connectionSuccessful){
			try {
			    Thread.sleep(5000);                 
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			nc.getTaskStatusUpdatesFromServer();
			try {
				Utils.openNewBrowserTab(new URL(dashboardUrl), "task-list");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

	}

	

}
