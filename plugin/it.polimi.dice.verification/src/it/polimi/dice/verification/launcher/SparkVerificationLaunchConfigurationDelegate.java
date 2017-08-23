package it.polimi.dice.verification.launcher;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import it.polimi.dice.verification.uml.diagrams.activitydiagram.NodeFactory;

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
		
		
		ResourceSet resourceSet = new ResourceSetImpl();
		
		Resource umlResource = resourceSet.getResource(URI.createFileURI(umlFile.getAbsolutePath()), true);
		//EList<EObject> inObjects = umlResource.getContents();
		for (Iterator<EObject> it = umlResource.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();
			if(eObject instanceof org.eclipse.uml2.uml.ActivityNode){
				NodeFactory.getInstance((org.eclipse.uml2.uml.ActivityNode) eObject);
			}
			
/*			if(eObject instanceof org.eclipse.uml2.uml.OpaqueAction){
				EList<Stereotype> stList = ((org.eclipse.uml2.uml.OpaqueAction) eObject).getAppliedStereotypes();
				if (UML2ModelHelper.isSparkMap((Element)eObject)) {
					SparkOperationNode stn = new SparkOperationNode((org.eclipse.uml2.uml.OpaqueAction)eObject);
					for (Node n : stn.getIncomingNodes()) {
						System.out.println(n);
					} 
//					SpoutClass sc = new SpoutClass((org.eclipse.uml2.uml.Class)eObject);
//					spouts.add(sc);
					System.out.println("Found SparkMap");
				}
				else if (UML2ModelHelper.isSparkReduce((Element)eObject)) {
//					BoltClass bc = new BoltClass((org.eclipse.uml2.uml.Class)eObject);
//					bolts.add(bc);
					System.out.println("Found SparkReduce");
				}
			}*/
		}
		
		
		
		
	}

	

}
