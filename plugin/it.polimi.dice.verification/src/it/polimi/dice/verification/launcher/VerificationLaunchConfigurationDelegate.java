package it.polimi.dice.verification.launcher;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
//import org.eclipse.acceleo.common.preference.AcceleoPreferences;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.uml2.uml.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.dice.core.logger.DiceLogger;
/*import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
*/
import it.polimi.dice.verification.DiceVerificationPlugin;
import it.polimi.dice.verification.httpclient.HttpClient;
import it.polimi.dice.verification.json.JsonVerificationContext;
import it.polimi.dice.verification.json.JsonVerificationTaskRequest;
import it.polimi.dice.verification.json.StormTopology;
import it.polimi.dice.verification.json.VerificationParameters;
import it.polimi.dice.verification.uml.diagrams.classdiagram.BoltClass;
import it.polimi.dice.verification.uml.diagrams.classdiagram.SpoutClass;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;
import it.polimi.dice.verification.verifiers.IVerifier;
import it.polimi.dice.verification.verifiers.VerificationException;
import it.polimi.dice.verification.verifiers.VerifierConstants;
import it.polimi.dice.vtconfig.VerificationToolConfig;
import it.polimi.dice.vtconfig.util.VerificationToolConfigSerializer;


public class VerificationLaunchConfigurationDelegate extends LaunchConfigurationDelegate {


	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Messages.VerificationLaunchConfigurationDelegate_verificationTaskTitle, IProgressMonitor.UNKNOWN);
			
			Map<String, String> verificationAttrs = new HashMap<>();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd__HH:mm:ss");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd__HH:mm:ss");
			//Date now = Calendar.getInstance().getTime();
			LocalDateTime now = LocalDateTime.now();
			verificationAttrs.put(DebugPlugin.ATTR_LAUNCH_TIMESTAMP, now.format(formatter)); 	
			VerificationToolConfig vtConfig = getVerificationToolConfig(configuration);
			int timeBound = configuration.getAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 15);
			DiceLogger.logError(DiceVerificationPlugin.getDefault(), "TIME BOUND: " + timeBound);
			
			final boolean keepIntermediateFiles = configuration.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false);
			final File intermediateFilesDir = getIntermediateFilesDir(configuration);
			
//			String templateFilePath = "/Users/francesco/Projects/DICE-Verification/json2mc/templating/templates/storm_topology_template.lisp";
			final File umlFile = getInputFile(configuration);
			final File configFile = Paths.get(intermediateFilesDir.toURI()).resolve("dump.vtconfig").toFile(); //$NON-NLS-1$
			final File jsonFile = Paths.get(intermediateFilesDir.toURI()).resolve("context.json").toFile(); //$NON-NLS-1$
//			final File resultFile = Paths.get(intermediateFilesDir.toURI()).resolve("result.txt").toFile(); //$NON-NLS-1$
//			final File templateFile = new File(templateFilePath);
//			final String json2mcPath = "/Users/francesco/Projects/DICE-Verification/json2mc/json2mc.py"; 
			
			try {
				try {
					dumpConfig(vtConfig, configFile, new SubProgressMonitor(monitor, 1));
					transformUmlToJson(umlFile, vtConfig, jsonFile, new SubProgressMonitor(monitor, 1), verificationAttrs, configuration);
				} finally {
					// Refresh workspace if intermediate files were stored in it
					if (keepIntermediateFiles) {
						for (IContainer container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(intermediateFilesDir.toURI())) {
							container.refreshLocal(IResource.DEPTH_ONE, new SubProgressMonitor(monitor, 1));
						}
					}
				}
			/*	String id = "it.polimi.dice.verification.zot"; //$NON-NLS-1$
				final IVerifier verifier = getVerifier(id);
				if (verifier == null) {
					throw new VerificationException(MessageFormat.format(Messages.VerificationLaunchConfigurationDelegate_verifierNotFoundError, id));
				}*/
				//String command = "python " + json2mcPath + " -t " + templateFilePath + " -c " + jsonFile.getAbsolutePath() + " -o /tmp";
				//String[] command = {"/bin/bash", "-c", json2mcPath /*+ " -t " + templateFilePath + " -c " + jsonFile.getAbsolutePath() + " -o /tmp"*/};
				//String[] command = {"python ", json2mcPath + " -t " + templateFilePath + " -c " + jsonFile.getAbsolutePath() + " -o /tmp"};
				//String[] command = {"/bin/bash", "-c", "ls -l"};
				String[] command = {"/bin/bash","zot /tmp/TEST/ae2sbvzot/zot_in.lisp"};
				DiceLogger.logError(DiceVerificationPlugin.getDefault(), "Launching " + command);
				//final Process p = Runtime.getRuntime().exec(command, null, resultFile);
				final Process p = Runtime.getRuntime().exec(command);
				new Thread(new Runnable() {
				    public void run() {
				     BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				     String line = null; 

				     try {
				        while ((line = input.readLine()) != null){
				        	DiceLogger.logError(DiceVerificationPlugin.getDefault(),"line ->" + line);
				        }
				        DiceLogger.logError(DiceVerificationPlugin.getDefault(), "outside the loop,  line is " + line);
				     } catch (IOException e) {
				            e.printStackTrace();
				     }
				    }
				}).start();
				DiceLogger.logError(DiceVerificationPlugin.getDefault(), "STARTED, WAIT");
				try {
					p.waitFor();
					DiceLogger.logError(DiceVerificationPlugin.getDefault(), "FINISHED");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				
				
//				String netName = new Path(gspnNetFile.getName()).removeFileExtension().toString();
				
/*	
  				// TODO: change this quick & dirty way to show the raw results
				final Process verificationProcess = verifier.verify("name", jsonFile);
				final RuntimeProcess runtimeProcess = new RuntimeProcess(launch, verificationProcess, getVerificationName(verifier.getId()), verificationAttrs);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							verificationProcess.waitFor();
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							IOUtils.copy(verifier.getRawResult(), out);
							runtimeProcess.setAttribute(DebugPlugin.ATTR_ENVIRONMENT, "*** VERIFICATION RAW RESULTS ***\n" + out.toString()); //$NON-NLS-1$
							
							try (FileWriter writer = new FileWriter(resultFile);) {
								writer.write(out.toString());
							} catch (IOException e) {
								DiceLogger.logException(DiceVerificationPlugin.getDefault(), e);
							}
						} catch (InterruptedException | IOException e) {
							DiceLogger.logException(DiceVerificationPlugin.getDefault(), e);
						} finally {
							// Refresh workspace if intermediate files were stored in it
							if (keepIntermediateFiles) {
								for (IContainer container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(intermediateFilesDir.toURI())) {
									try {
										container.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
									} catch (CoreException e) {
										DiceLogger.logException(DiceVerificationPlugin.getDefault(), e);
									}
								}
							}
						}
					}
				}).start();
*/				
			} catch (IOException /*| VerificationException*/ e) {
				throw new CoreException(new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, e.getLocalizedMessage(), e));
			}
		} finally {
			monitor.done();
		}
	}
	
	private static void openUrl(URL url, String browserId) {
	    IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
	    IWebBrowser browser;
	    try {
	        browser = support.createBrowser(DiceVerificationPlugin.PLUGIN_ID + "_" + browserId);
	        browser.openURL(url);
	    } catch (PartInitException e) {
	        DiceLogger.logException(DiceVerificationPlugin.getDefault(), e);
	    }
	}
	 
	
	public static void openNewBrowserTab(URL url, String browserId){
		Display.getDefault().syncExec(new Runnable() { 
			public void run() { 
					openUrl(url, browserId);
				} 
			}); 
	}
	
	
	
	
	private File getInputFile(ILaunchConfiguration configuration) throws CoreException {
		String inputFileUriString = configuration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY);
		java.net.URI inputFileUri;
		inputFileUri = java.net.URI.create(inputFileUriString);
		File inputFile = new File(inputFileUri);
		if (!inputFile.isFile()) {
			throw new CoreException(new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, 
					MessageFormat.format(Messages.VerificationLaunchConfigurationDelegate_invalidLocationError, inputFile)));
		}
		return inputFile;
	}
	
	private File getIntermediateFilesDir(ILaunchConfiguration configuration) throws CoreException {
		File intermediateFilesDir;
		if (configuration.getAttribute(VerificationLaunchConfigurationAttributes.KEEP_INTERMEDIATE_FILES, false)) {
			String intermediateFilesDirUriString = configuration.getAttribute(VerificationLaunchConfigurationAttributes.INTERMEDIATE_FILES_DIR, StringUtils.EMPTY);
			java.net.URI intermediateFilesDirUri;
			intermediateFilesDirUri = java.net.URI.create(intermediateFilesDirUriString);
			intermediateFilesDir = new File(intermediateFilesDirUri);
		} else {
			try {
				intermediateFilesDir = Files.createTempDirectory("dice-verification-", new FileAttribute[] {}).toFile(); //$NON-NLS-1$
				intermediateFilesDir.deleteOnExit();
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, 
						Messages.VerificationLaunchConfigurationDelegate_unableCreateTempFileError, e));
			}
		}
		if (!intermediateFilesDir.isDirectory()) {
			throw new CoreException(new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, 
					MessageFormat.format(Messages.VerificationLaunchConfigurationDelegate_invalidLocationIntermediateFilesError, intermediateFilesDir)));
		}
		return intermediateFilesDir;
	}
	

	private VerificationToolConfig getVerificationToolConfig(ILaunchConfiguration configuration) throws CoreException {
		String serializedConfig = configuration.getAttribute(VerificationLaunchConfigurationAttributes.VERIFICATION_CONFIGURATION, StringUtils.EMPTY);
		DiceLogger.logError(DiceVerificationPlugin.getDefault(), "SERIALIZED-CONFIG:\n" + serializedConfig);
		try {
			return VerificationToolConfigSerializer.deserialize(serializedConfig);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DiceVerificationPlugin.PLUGIN_ID, 
					MessageFormat.format(Messages.VerificationLaunchConfigurationDelegate_unableDeserializeError, serializedConfig), e));
		}
	}

	private void dumpConfig(VerificationToolConfig vtConfig, File vtConfigFile, IProgressMonitor monitor) throws IOException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Messages.VerificationLaunchConfigurationDelegate_dumpingTaskTitle, 1);
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource configResource = resourceSet.createResource(URI.createFileURI(vtConfigFile.getAbsolutePath()));
			configResource.getContents().add(vtConfig);
			configResource.save(Collections.emptyMap());
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
	
	
	
	/*public void verifyZOTFile() throws IOException, InterruptedException{
		//TODO Can we do better?
		Process proc=null;
		File wd=new File(this.TMP_DIR);
		String[] cmd = {"/bin/bash", "-c", "zot "+this.ZOT_MODEL_FILE+" > "+this.ZOT_OUTPUT_FILE};
		proc = Runtime.getRuntime().exec(cmd, null, wd);
		LOGGER.info("Waiting for ZOT to finish");
		proc.waitFor();
		DiceLogger.logError(DiceVerificationPlugin.getDefault(), "WRITING FILE TO:\n" + jsonFile.getAbsolutePath());
		LOGGER.info("ZOT to finished");
	}*/

	private void transformUmlToJson(File umlFile, VerificationToolConfig vtConfig, File jsonFile, IProgressMonitor monitor, Map<String, String> attributes, ILaunchConfiguration launchConfig) throws IOException {
		
		JsonVerificationContext jsonContext;
		VerificationParameters vp = new VerificationParameters(); 
		List<SpoutClass> spouts = new ArrayList<>();
		List<BoltClass> bolts = new ArrayList<>();
		StormTopology topology = new StormTopology();
		Gson gson = new Gson();
		
		String filename = umlFile.getName();
		int pos = filename.lastIndexOf(".");
		String justName = pos > 0 ? filename.substring(0, pos) : filename;			
		String timestamp = attributes.get(DebugPlugin.ATTR_LAUNCH_TIMESTAMP);
		String verificationIdentifier = justName + "_" + timestamp;
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Messages.VerificationLaunchConfigurationDelegate_generatingJsonTaskTitle, 1);
			ResourceSet resourceSet = new ResourceSetImpl();
			
//			TransformationExecutor executor = new TransformationExecutor(URI.createURI(PnmlM2mPlugin.AD2PNML_TRANSFORMATION_URI));
	
//			ExecutionContextImpl context = new ExecutionContextImpl();
			
			Resource umlResource = resourceSet.getResource(URI.createFileURI(umlFile.getAbsolutePath()), true);
			//EList<EObject> inObjects = umlResource.getContents();
			for (Iterator<EObject> it = umlResource.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				if(eObject instanceof org.eclipse.uml2.uml.Class){
					if (UML2ModelHelper.isSpout((Element)eObject)) {
						SpoutClass sc = new SpoutClass((org.eclipse.uml2.uml.Class)eObject);
						spouts.add(sc);
					}
					else if (UML2ModelHelper.isBolt((Element)eObject)) {
						BoltClass bc = new BoltClass((org.eclipse.uml2.uml.Class)eObject);
						bolts.add(bc);
					}
				}
			}
			
			topology.setSpouts(spouts);
			topology.setBolts(bolts);
			vp.setPeriodicQueuesFromBolts(bolts);
			try {
				vp.setTimeBound(launchConfig.getAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 15));
			} catch (CoreException e) {
				e.printStackTrace();
			}
			ArrayList<String> plugins = new ArrayList<String>();
			plugins.add(vtConfig.getZotPlugin().getName());
			vp.setPlugins(plugins);
			List<String> monitoredBoltsList = new ArrayList<String>();//(vtConfig.getMonitoredBolts().keySet());
			EMap<String, Boolean> monitoredBoltsMap = vtConfig.getMonitoredBolts();
			for (String key : monitoredBoltsMap.keySet()) {
				if(monitoredBoltsMap.get(key))
					monitoredBoltsList.add(key);
			}
			vp.setStrictlyMonotonicQueues(monitoredBoltsList);
			jsonContext = new JsonVerificationContext(topology, vp);
			jsonContext.setApplicationName(verificationIdentifier);
			
			DiceLogger.logError(DiceVerificationPlugin.getDefault(), "JSON CONTEXT CREATED:\n" + gson.toJson(jsonContext));
			///Resource jsonResource = resourceSet.createResource(URI.createFileURI(jsonFile.getAbsolutePath()));
			//jsonResource.getContents().add(t);
			//jsonResource.save(Collections.emptyMap());
			String launchVerificationUrl = "http://localhost:5000/longtasks";
			String taskListURL = "http://localhost:5000/";
			//String jsonRequest = "{\"title\":\"pinellaxJAVA\",\"json_context\":{\"verification_params\": {\"base_quantity\": 10, \"periodic_queues\": [\"expander\"], \"num_steps\": 20, \"max_time\": 20000, \"plugin\": [\"ae2bvzot\", \"ae2sbvzot\"]}, \"version\": \"0.1\", \"app_name\": \"SIMPLIFIED FOCUSED CRAWLER\", \"topology\": {\"bolts\": [{\"d\": 0.0, \"parallelism\": 4, \"min_ttf\": 1000, \"alpha\": 0.5, \"sigma\": 2.0, \"id\": \"WpDeserializer\", \"subs\": [\"wpSpout\"]}, {\"d\": 0.0, \"parallelism\": 8, \"min_ttf\": 1000, \"alpha\": 3.0, \"sigma\": 0.75, \"id\": \"expander\", \"subs\": [\"WpDeserializer\"]}, {\"d\": 0.0, \"parallelism\": 1, \"min_ttf\": 1000, \"alpha\": 1.0, \"sigma\": 1.0, \"id\": \"articleExtraction\", \"subs\": [\"expander\"]}, {\"d\": 0.0, \"parallelism\": 1, \"min_ttf\": 1000, \"alpha\": 1.0, \"sigma\": 1.0, \"id\": \"mediaExtraction\", \"subs\": [\"expander\"]}], \"init_queues\": 4, \"max_reboot_time\": 100, \"max_idle_time\": 1.0, \"min_reboot_time\": 10, \"spouts\": [{\"avg_emit_rate\": 4.0, \"id\": \"wpSpout\"}], \"queue_threshold\": 0}, \"description\": \"\"}}";

			JsonVerificationTaskRequest vtr = new JsonVerificationTaskRequest(verificationIdentifier, jsonContext);
			
			Gson gsonBuilder = new GsonBuilder().create();
			DiceLogger.logError(DiceVerificationPlugin.getDefault(), "WRITING FILE TO:\n" + jsonFile.getAbsolutePath());
			try (Writer writer = new FileWriter(jsonFile)) {    
			    gsonBuilder.toJson(jsonContext, writer);
			}
			
			HttpClient nc = new HttpClient();
			boolean connectionSuccessful;
			connectionSuccessful = nc.postJSONRequest(launchVerificationUrl, gsonBuilder.toJson(vtr));
			
			if (connectionSuccessful){
				try {
				    Thread.sleep(5000);                 
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				nc.getTaskStatusUpdatesFromServer();
				openNewBrowserTab(new URL(taskListURL), "task-list");
			}
			
		}finally{
			monitor.done();
		}
		
	}
	
/*	private void transformUmlToJson(File umlFile, VerificationToolConfig vtConfig, File jsonFile, IProgressMonitor monitor) throws IOException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Messages.VerificationLaunchConfigurationDelegate_generatingPnmlTaskTitle, 1);
			ResourceSet resourceSet = new ResourceSetImpl();
			
			TransformationExecutor executor = new TransformationExecutor(URI.createURI(PnmlM2mPlugin.AD2PNML_TRANSFORMATION_URI));
	
			ExecutionContextImpl context = new ExecutionContextImpl();
			
			Resource umlResource = resourceSet.getResource(URI.createFileURI(umlFile.getAbsolutePath()), true);
			EList<EObject> inObjects = umlResource.getContents();
	
			ModelExtent ad = new BasicModelExtent(inObjects);
			ModelExtent config = new BasicModelExtent(Arrays.asList(new EObject[] { vtConfig }));
			ModelExtent res = new BasicModelExtent();
	
			ExecutionDiagnostic result = executor.execute(context, ad, config, res);
	
			if (result.getSeverity() == Diagnostic.OK) {
				Resource pnmlResource = resourceSet.createResource(URI.createFileURI(jsonFile.getAbsolutePath()));
				pnmlResource.getContents().addAll(res.getContents());
				pnmlResource.save(Collections.emptyMap());
			} else {
//				DiceLogger.log(DiceVerificationPlugin.getDefault(), BasicDiagnostic.toIStatus(result));
			}
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
*/
	/*private void transformPnmlToGspn(File pnmlFile, File intermediateFilesDir, IProgressMonitor monitor) throws IOException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Messages.VerificationLaunchConfigurationDelegate_generatingGspnTaskTitle, 1);
			GenerateGspn gspnGenerator = new GenerateGspn(URI.createFileURI(pnmlFile.getAbsolutePath()), intermediateFilesDir, new ArrayList<EObject>());
			AcceleoPreferences.switchForceDeactivationNotifications(true);
			gspnGenerator.doGenerate(BasicMonitor.toMonitor(new SubProgressMonitor(monitor, 1)));
		} finally {
			monitor.done();
		}
	}*/
	
	
	private IVerifier getVerifier(String id) throws CoreException {
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(VerifierConstants.EXTENSION_ID);
		
		for (IConfigurationElement configElement : configElements) {
			String configId = configElement.getAttribute(VerifierConstants.ID_ATTR);
			if (StringUtils.equals(id, configId)) {
				return (IVerifier) configElement.createExecutableExtension(VerifierConstants.VERIFIER_ATTR);
			}
		}
		return null;
	}
	

	private static String getVerificationName(String id) {
		return MessageFormat.format(Messages.VerificationLaunchConfigurationDelegate_verificationName, id.toString());
	}
}
