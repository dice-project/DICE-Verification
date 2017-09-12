package it.polimi.dice.verification.ui.launcher.storm;

import java.text.MessageFormat;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.uml.tools.model.UmlModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import it.polimi.dice.core.logger.DiceLogger;
import it.polimi.dice.core.ui.dialogs.DialogUtils;
import it.polimi.dice.verification.DiceVerificationPlugin;
import it.polimi.dice.verification.ui.preferences.PreferenceConstants;
import it.polimi.dice.verification.uml.helpers.DiceProfileConstants;
import it.polimi.dice.verification.uml.helpers.UML2ModelHelper;
import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationAttributes;
import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;
import it.polimi.dice.verification.ui.launcher.common.Messages;


public class StormVerificationLaunchShortcut implements ILaunchShortcut {

	
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
		if (input != null){
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file != null) {
				launch(file, mode);
			}	
		}
	}

	protected void launch(Object type, String mode) {
		UmlModel model = type instanceof UmlModel ? (UmlModel) type : ((IAdaptable) type).getAdapter(UmlModel.class);
		if (model == null) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(),
					MessageFormat.format(Messages.VerificationLaunchShortcut_unexpectedArgError, 
					DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE_STORM,model));
			return;
		}
		
		if (!ILaunchManager.RUN_MODE.equals(mode)) {
			DiceLogger.logWarning(DiceVerificationUiPlugin.getDefault(),
					MessageFormat.format(Messages.VerificationLaunchShortcut_unknownModeError, mode, model));
		}

		if (!UML2ModelHelper.hasProfileApplied(model.getURI().toString(), DiceProfileConstants.STORM_PROFILE_NAME)) {
			DiceLogger.logError(DiceVerificationUiPlugin.getDefault(),
					MessageFormat.format(Messages.VerificationLaunchShortcut_missingProfileMessage,
							DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE_STORM,
							DiceProfileConstants.STORM_PROFILE_NAME));
			DialogUtils.getWarningDialog(Messages.VerificationLaunchShortcut_missingProfileText,
					MessageFormat.format(Messages.VerificationLaunchShortcut_missingProfileMessage,
							DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE_STORM,
							DiceProfileConstants.STORM_PROFILE_NAME));
			return;
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
		
		ILaunchConfigurationType verLaunchConfigurationType = launchManager.getLaunchConfigurationType(DiceVerificationPlugin.VERIFICATION_LAUNCH_CONFIGURATION_TYPE_STORM);
		
		ILaunchConfiguration[] existingConfigs = launchManager.getLaunchConfigurations(verLaunchConfigurationType);
		
		// We search through the existing configurations if the actual configuration has been previously defined
		for (ILaunchConfiguration previousConfiguration : existingConfigs) {
			String previousFile = previousConfiguration.getAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, StringUtils.EMPTY); 
			if (previousFile.equals(model.getURI().toString())) {
				return previousConfiguration;
			}
		}
		
		String name = model.getURI().trimFileExtension().lastSegment();
		String casedName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		ILaunchConfigurationWorkingCopy launchConf = verLaunchConfigurationType.newInstance(null, casedName);
		launchConf.setAttribute(VerificationLaunchConfigurationAttributes.INPUT_FILE, model.getURI().toString());
		launchConf.setAttribute(VerificationLaunchConfigurationAttributes.TIME_BOUND, 15);
		launchConf.setAttribute(VerificationLaunchConfigurationAttributes.HOST_ADDRESS, 
									DiceVerificationUiPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.HOST.getName()));
		launchConf.setAttribute(VerificationLaunchConfigurationAttributes.PORT_NUMBER, 
									DiceVerificationUiPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.PORT.getName()));
		ILaunchConfiguration result = launchConf.doSave();
		
		return result;
	}

	  


}
