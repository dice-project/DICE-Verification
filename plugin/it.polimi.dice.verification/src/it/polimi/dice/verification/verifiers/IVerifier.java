package it.polimi.dice.verification.verifiers;

import java.io.File;
import java.io.InputStream;


public interface IVerifier {
	
	public String getId();
	
	public Process verify(String subject, File... inputFiles) throws VerificationException;

	public InputStream getRawResult();

}
