package it.polimi.dice.verification.launcher;

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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

public class SampleLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	  @Override
	  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
	      throws CoreException {
		  
		  
		
	    String attribute = configuration.getAttribute(VerificationLaunchConfigurationAttributes.CONSOLE_TEXT, "Simon says \"RUN!\"");
	    System.out.println(attribute);
	    
		PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
		    Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		    MessageDialog.openConfirm(activeShell, "Confirm", attribute);
		});

	  }

		public static Model loadModel(String pathToModel){
			//A collection of related persistent documents.
			ResourceSet set = new ResourceSetImpl();
			
/*			//Register the UML Package
			set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
			set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
			set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.PROFILE_FILE_EXTENSION, UMLResource.Factory.INSTANCE );
			
			Map<URI,URI> uriMap = set.getURIConverter().getURIMap();
//			URI uml_resource_uri = URI.createPlatformPluginURI("it.polimi.dice.profiles", true);
			URI uml_resource_uri = URI.createPlatformPluginURI("org.correttouml.profiles", true);
			uriMap.put(URI.createURI("pathmap://resources/"), uml_resource_uri.appendSegment("resources").appendSegment(""));
			
			uml_resource_uri = URI.createPlatformPluginURI("org.eclipse.uml2.uml.resources", true);
			uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uml_resource_uri.appendSegment("libraries").appendSegment(""));
			uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uml_resource_uri.appendSegment("metamodels").appendSegment(""));
			uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uml_resource_uri.appendSegment("profiles").appendSegment(""));
*/			
			//Add the model file to the resource set
			URI uri = URI.createFileURI(pathToModel);
			set.createResource(uri);
			Resource r = set.getResource(uri, true);
			
			for(EObject o: r.getContents()){
				System.out.println(o);
				EcoreUtil.resolveAll(o);
			}
			
			Model m=(Model)EcoreUtil.getObjectByType(r.getContents(), UMLPackage.eINSTANCE.getModel());
			return m;
		}

	} 
