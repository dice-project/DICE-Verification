package it.polimi.dice.verification.ui.launcher.spark;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import it.polimi.dice.verification.launcher.SparkAnalysisType;

public class SparkVerificationFormData {
	private String taskName;
	private String taskDescription;
	private String inputFile;
	private boolean keepIntermediateFiles;
	private String intermediateFilesDir;
	private SparkAnalysisType analysisType = SparkAnalysisType.FEASIBILITY;
	private int deadline;
	private int timeBound;
	private String hostAddress;
	private String portNumber;
	
	private SparkMainLaunchConfigurationTab tab;
	
	public SparkVerificationFormData(SparkMainLaunchConfigurationTab tab){
		this.tab = tab;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName, boolean refresh) {
		this.taskName = taskName;
		if (refresh)
			//taskNameText.setText(taskName);
			tab.setTaskNameText(taskName);
		tab.setTabDirty(true);
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription, boolean refresh) {
		this.taskDescription = taskDescription;
		if (refresh)
			tab.setTaskDescriptionText(taskDescription);
			//taskDescriptionText.setText(taskDescription);
		tab.setTabDirty(true);
	}

	protected String getInputFile() {
		return inputFile;
	}

	protected void setInputFile(String inputFile) {
		this.inputFile = inputFile;
		String readableInputFile = toReadableString(inputFile);
		tab.setInputFileText(readableInputFile != null ? readableInputFile : StringUtils.EMPTY);
		//inputFileText.setText(readableInputFile != null ? readableInputFile : StringUtils.EMPTY);
		tab.setTabDirty(true);
	}

	protected String getIntermediateFilesDir() {
		return intermediateFilesDir;
	}

	protected void setIntermediateFilesDir(String intermediateFilesDir) {
		this.intermediateFilesDir = intermediateFilesDir;
		String readableFilesDir = toReadableString(intermediateFilesDir);
		tab.setIntermediateFilesDirText(readableFilesDir != null ? readableFilesDir : StringUtils.EMPTY);
		//intermediateFilesDirText.setText(readableFilesDir != null ? readableFilesDir : StringUtils.EMPTY);
		tab.setTabDirty(true);
	}

	public SparkAnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(SparkAnalysisType analysisType, boolean refresh) {
		this.analysisType = analysisType;
		if (refresh)
			tab.setAnalysisTypeButtons(analysisType);
			//setAnalysisTypeButtons(analysisType);
		tab.setTabDirty(true);
	}

	

	public int getDeadline() {
		return deadline;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
		tab.setDeadlineSpinner(deadline);
		//deadlineSpinner.setSelection(deadline);
		tab.setTabDirty(true);
	}

	protected int getTimeBound() {
		return timeBound;
	}

	protected void setTimeBound(int timeBound) {
		this.timeBound = timeBound;
		tab.setTimeBoundSpinner(timeBound);
		//timeBoundSpinner.setSelection(timeBound);
		tab.setTabDirty(true);
	}

	public void setHostAddress(String hostAddress, boolean refresh) {
		this.hostAddress = hostAddress;
		if (refresh)
			tab.setHostText(hostAddress);
			//hostText.setText(hostAddress);
		tab.setTabDirty(true);
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setPortNumber(String portNumber, boolean refresh) {
		this.portNumber = portNumber;
		if (refresh)
			tab.setPortText(portNumber);
			//portText.setText(portNumber);
		tab.setTabDirty(true);
	}

	public String getPortNumber() {
		return portNumber;
	}

	protected boolean keepIntermediateFiles() {
		return keepIntermediateFiles;
	}

	protected void setKeepIntermediateFiles(boolean keepIntermediateFiles) {
		this.keepIntermediateFiles = keepIntermediateFiles;
		tab.setKeepIntermediateFilesButton(keepIntermediateFiles);
		//keepIntermediateFilesButton.setSelection(keepIntermediateFiles);
		//intermediateFilesDirText.setEnabled(keepIntermediateFiles);
		//browseIntermediateFilesDirButton.setEnabled(keepIntermediateFiles);
		tab.setTabDirty(true);
	}

	private String toReadableString(String fileUriString) {
		URI uri = URI.create(fileUriString);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (new File(uri).isFile()) {
			IFile[] files = root.findFilesForLocationURI(uri);
			if (files.length > 0) {
				return org.eclipse.emf.common.util.URI
						.createPlatformResourceURI(files[0].getFullPath().toString(), false).toString();
			}
		} else {
			IContainer[] containers = root.findContainersForLocationURI(uri);
			if (containers.length > 0) {
				return org.eclipse.emf.common.util.URI
						.createPlatformResourceURI(containers[0].getFullPath().toString(), false).toString();
			}
		}
		return null;
	}

}