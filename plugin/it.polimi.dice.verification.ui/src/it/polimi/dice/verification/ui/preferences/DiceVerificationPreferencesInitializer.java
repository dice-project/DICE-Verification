package it.polimi.dice.verification.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;

public class DiceVerificationPreferencesInitializer extends AbstractPreferenceInitializer {

	  public DiceVerificationPreferencesInitializer() {
	 	}

	  @Override
	  public void initializeDefaultPreferences() {
	    IPreferenceStore store = DiceVerificationUiPlugin.getDefault().getPreferenceStore();
	    store.setDefault(PreferenceConstants.HOST.getName(), "http://localhost");
	    store.setDefault(PreferenceConstants.PORT.getName(), "5000");
	    
	  }


}
