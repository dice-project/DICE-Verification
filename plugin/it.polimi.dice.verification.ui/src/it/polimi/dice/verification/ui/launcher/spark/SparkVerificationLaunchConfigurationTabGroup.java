package it.polimi.dice.verification.ui.launcher.spark;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class SparkVerificationLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public SparkVerificationLaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new ILaunchConfigurationTab[] {new CommonTab() });
	}

}
