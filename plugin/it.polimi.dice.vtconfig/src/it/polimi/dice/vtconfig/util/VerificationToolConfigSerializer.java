package it.polimi.dice.vtconfig.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import it.polimi.dice.vtconfig.VerificationToolConfig;

public class VerificationToolConfigSerializer {

	public static String serialize(VerificationToolConfig config) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Resource resource = new XMIResourceImpl();
		resource.getContents().add(config);
		try {
			resource.save(stream, Collections.emptyMap());
		} catch (IOException e) {
			// Should not happen on a ByteArrayOutputStream
		}
		
		return stream.toString();
	}

	public static VerificationToolConfig deserialize(String serializedConfig) throws IOException {
		Resource resource = new XMIResourceImpl();
		resource.load(new ByteArrayInputStream(serializedConfig.getBytes()), Collections.emptyMap());
		return (VerificationToolConfig) resource.getContents().get(0);
	}
	
}
