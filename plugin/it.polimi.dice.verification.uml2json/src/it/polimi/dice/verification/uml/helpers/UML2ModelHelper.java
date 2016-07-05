package it.polimi.dice.verification.uml.helpers;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

public class UML2ModelHelper {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(UML2ModelHelper.class);
	
	/** The source UML model */
	private Model model = null;

	/** Get a Eclipse UML2 model */
	public UML2ModelHelper(Model model) {
		this.model = model;
	}

	/** Save the Decorated UML2 model to a file */	
	public void saveModel(String file){
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createFileURI(file));
        resource.getContents().add(model);
        try {
			resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Given a file containing the Eclipse UML2 Model returns the model */
	public static Model loadModel(String pathToModel){
		//A collection of related persistent documents.
		ResourceSet set = new ResourceSetImpl();
		
/*		//Register the UML Package
		set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.PROFILE_FILE_EXTENSION, UMLResource.Factory.INSTANCE );
		
		Map<URI,URI> uriMap = set.getURIConverter().getURIMap();
		URI uml_resource_uri = URI.createPlatformPluginURI("it.polimi.dice.profiles", true);
		uriMap.put(URI.createURI("pathmap://resources/"), uml_resource_uri.appendSegment("resources").appendSegment(""));
		
		uml_resource_uri = URI.createPlatformPluginURI("org.eclipse.uml2.uml.resources", true);
		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uml_resource_uri.appendSegment("libraries").appendSegment(""));
		uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uml_resource_uri.appendSegment("metamodels").appendSegment(""));
		uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uml_resource_uri.appendSegment("profiles").appendSegment(""));
		
*/		//Add the model file to the resource set
		URI uri = URI.createFileURI(pathToModel);
		set.createResource(uri);
		Resource r = set.getResource(uri, true);
		
		Model m=(Model)EcoreUtil.getObjectByType(r.getContents(), UMLPackage.eINSTANCE.getModel());
		return m;
	}
	
	public static boolean hasStereotype(Element e, String name){
		for(Stereotype st: e.getAppliedStereotypes()){
			if(st.getName().equals(name)) return true;
		}
		return false;
	}
	
	public static boolean isSpoutInstance(Element e){
		if(e instanceof InstanceSpecification){
			InstanceSpecification is=(InstanceSpecification) e;
			for(Classifier c: is.getClassifiers()){
				if(isSpoutType(c)) return true;
			}
		}
		return false;
	}

	public static boolean isBoltInstance(Element e){
		if(e instanceof InstanceSpecification){
			InstanceSpecification is=(InstanceSpecification) e;
			for(Classifier c: is.getClassifiers()){
				if(isBoltType(c)) return true;
			}
		}
		return false;
	}

	public static boolean isBolt(Element e){
		if(hasStereotype(e, "Bolt")) 
			return true;
		return false;
	}
	
	public static boolean isStreamSubscription(Element e){
		if(hasStereotype(e, "StreamSubscription")) 
			return true;
		return false;
	}

	public static boolean isStream(Element e){
		if(hasStereotype(e, "Stream")) 
			return true;
		return false;
	}
	
	
	public static boolean isSpout(Element e){
		if(hasStereotype(e, "Spout")) 
			return true;
		return false;
	}
	
	
	
	
	private static boolean isSpoutType(Classifier c) {
		return hasStereotype(c, "SpoutType");
	}
	
	private static boolean isBoltType(Classifier c) {
		return hasStereotype(c, "BoltType");
	}


	public static Stereotype getStereotype(Element e, String name){
		for(Stereotype st: e.getAppliedStereotypes()){
			if(st.getName().equals(name)) return st;
		}
		return null;
	}


}