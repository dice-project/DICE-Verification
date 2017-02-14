package it.polimi.dice.verification.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;

public class DiceVerificationPreferencesView extends ViewPart {

	private Label label;
	
	public void createPartControl(Composite parent) {
	    IPreferenceStore preferenceStore = DiceVerificationUiPlugin.getDefault()
	        .getPreferenceStore();
	    String string = preferenceStore.getString("server-port");

	    label = new Label(parent, SWT.NONE);
	    label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
	        false));
	    label.setText(string);
	    // add change listener to the preferences store so that we are notified
	    // in case of changes
	    DiceVerificationUiPlugin.getDefault().getPreferenceStore()
	        .addPropertyChangeListener(new IPropertyChangeListener() {
	          @Override
	          public void propertyChange(PropertyChangeEvent event) {
	            if (event.getProperty() == "server-port") {
	              label.setText(event.getNewValue().toString());
	            }
	          }
	        });
	  }
	


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
