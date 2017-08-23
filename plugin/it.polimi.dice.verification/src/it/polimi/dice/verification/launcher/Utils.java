package it.polimi.dice.verification.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.verification.DiceVerificationPlugin;

public class Utils {

	public static File getInputFile(ILaunchConfiguration configuration) throws CoreException {
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
	
	public static File getIntermediateFilesDir(ILaunchConfiguration configuration) throws CoreException {
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
	
	
	public static void openUrl(URL url, String browserId) {
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
			@Override
			public void run() { 
					openUrl(url, browserId);
				} 
			}); 
	}
}
