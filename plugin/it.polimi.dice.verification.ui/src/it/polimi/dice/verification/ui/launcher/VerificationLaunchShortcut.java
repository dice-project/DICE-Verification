package it.polimi.dice.verification.ui.launcher;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.uml.tools.model.UmlModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;

import it.polimi.dice.core.logger.DiceLogger;

import it.polimi.dice.verification.DiceVerificationPlugin;
import it.polimi.dice.verification.ui.launcher.Messages;
import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationDelegate;
import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;


public class VerificationLaunchShortcut implements ILaunchShortcut {

	
	 @Override
	  public void launch(ISelection selection, String mode) {
	    System.out.println("Selection: " + mode);
	    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		    public void run() {
			    Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			    MessageDialog.openConfirm(activeShell, "Confirm", "Selection: " + mode);
			}
		});
	  }
	 
	  @Override
	  public void launch(IEditorPart editor, String mode) {
	    System.out.println("Editor: " + mode);
	    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		    public void run() {
			    Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			    MessageDialog.openConfirm(activeShell, "Confirm", "Editor: " + mode);
			}
		});
	  }

	/*
	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (!structuredSelection.isEmpty()) {
				launch(structuredSelection.getFirstElement(), mode);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file != null) {
			launch(file, mode);
		}
	}

	protected void launch(Object type, String mode) {
		UmlModel model = type instanceof UmlModel ? (UmlModel) type : ((IAdaptable) type).getAdapter(UmlModel.class);
		if (model == null) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(),
					MessageFormat.format(Messages.VerificationLaunchShortcut_unexpectedArgError, 
					DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE,model));
			return;
		}
		
		if (!ILaunchManager.RUN_MODE.equals(mode)) {
			DiceLogger.logWarning(DiceVerificationUiPlugin.getDefault(),
					MessageFormat.format(Messages.VerificationLaunchShortcut_unknownModeError, mode, model));
		}

		try {
            ILaunchConfiguration launchConfiguration = findLaunchConfiguration(model, mode);
            if (launchConfiguration != null) {
				Shell shell =  PlatformUI.getWorkbench().getDisplay().getActiveShell();
				DebugUITools.openLaunchConfigurationDialogOnGroup(
						shell, new StructuredSelection(launchConfiguration), IDebugUIConstants.ID_RUN_LAUNCH_GROUP, null);
			} 
        } catch (CoreException e) {
        DiceLogger.logException(DiceVerificationUiPlugin.getDefault(), e);
        }
    }

	
	protected ILaunchConfiguration findLaunchConfiguration(UmlModel model, String mode) throws CoreException {
		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		
		ILaunchConfigurationType simLaunchConfigurationType = launchManager.getLaunchConfigurationType(DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE);
		
		ILaunchConfiguration[] existingConfigs = launchManager.getLaunchConfigurations(simLaunchConfigurationType);
		
		// We search through the existing configurations if the actual configuration has been previously defined
		for (ILaunchConfiguration previousConfiguration : existingConfigs) {
			String previousFile = previousConfiguration.getAttribute(VerificationLaunchConfigurationDelegate.INPUT_FILE, StringUtils.EMPTY); 
			if (previousFile.equals(model.getURI().toString())) {
				return previousConfiguration;
			}
		}
		
		String name = model.getURI().trimFileExtension().lastSegment();
		String casedName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		ILaunchConfigurationWorkingCopy launchConf = simLaunchConfigurationType.newInstance(null, casedName);
		launchConf.setAttribute(VerificationLaunchConfigurationDelegate.INPUT_FILE, model.getURI().toString());
		ILaunchConfiguration result = launchConf.doSave();
		
		return result;
	}
*/
	  


}