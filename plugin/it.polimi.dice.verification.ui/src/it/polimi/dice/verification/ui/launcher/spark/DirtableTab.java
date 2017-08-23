package it.polimi.dice.verification.ui.launcher.spark;

import org.eclipse.swt.widgets.Shell;

public interface DirtableTab {
	
	public void setTabDirty(boolean dirty);
	
	public Shell getTabShell();
	
}
