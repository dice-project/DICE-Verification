package it.polimi.dice.verification.ui.preferences;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;

public class DiceVerificationShowPreferenceValues extends AbstractHandler {

	@Override
	  public Object execute(ExecutionEvent event) throws ExecutionException {
	    Shell shell = HandlerUtil.getActiveWorkbenchWindowChecked(event)
	        .getShell();
	    String serverIp = DiceVerificationUiPlugin.getDefault().getPreferenceStore()
	        .getString(PreferenceConstants.HOST.getName());
	    MessageDialog.openInformation(shell, "Info", serverIp);
	    String serverPort = DiceVerificationUiPlugin.getDefault().getPreferenceStore()
		        .getString(PreferenceConstants.PORT.getName());
		    MessageDialog.openInformation(shell, "Info", serverPort);

		    return null;
	  }
}
