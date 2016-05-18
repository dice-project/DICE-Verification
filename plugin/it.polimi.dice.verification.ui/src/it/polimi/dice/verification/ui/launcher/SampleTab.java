package it.polimi.dice.verification.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import it.polimi.dice.verification.launcher.VerificationLaunchConfigurationAttributes;

public class SampleTab extends AbstractLaunchConfigurationTab {

	 private Text text;

	  @Override
	  public void createControl(Composite parent) {
	    
	    Composite comp = new Group(parent, SWT.BORDER);
	    setControl(comp);
	    
	    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

	    Label label = new Label(comp, SWT.NONE);
	    label.setText("Console Text:");
	    GridDataFactory.swtDefaults().applyTo(label);

	    text = new Text(comp, SWT.BORDER);
	    text.setMessage("Console Text");
	    GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
	  }

	  @Override
	  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	  }

	  @Override
	  public void initializeFrom(ILaunchConfiguration configuration) {
	    try {
	      String consoleText = configuration.getAttribute(VerificationLaunchConfigurationAttributes.CONSOLE_TEXT,
	          "Simon says \"RUN!\"");
	      text.setText(consoleText);
	    } catch (CoreException e) {
	      // ignore here
	    }
	  }

	  @Override
	  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	    // set the text value for the CONSOLE_TEXT key
	    configuration.setAttribute(VerificationLaunchConfigurationAttributes.CONSOLE_TEXT, text.getText());
	  }

	  @Override
	  public String getName() {
	    return "Verification launch tab";
	  }

	} 