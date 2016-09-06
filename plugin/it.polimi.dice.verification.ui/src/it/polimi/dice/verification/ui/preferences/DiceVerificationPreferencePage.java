package it.polimi.dice.verification.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import it.polimi.dice.verification.ui.DiceVerificationUiPlugin;

public class DiceVerificationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DiceVerificationPreferencePage() {
		super(GRID);
		setPreferenceStore(DiceVerificationUiPlugin.getDefault().getPreferenceStore());
		setDescription("Basic Prefererences for DICE verification tool");
	}

	@Override
	public void init(IWorkbench workbench) {
		 
		
	}

	@Override
	protected void createFieldEditors() {
	    addField(new StringFieldEditor(PreferenceConstants.HOST.getName(), PreferenceConstants.HOST.getDescription(),
	            getFieldEditorParent()));
	    addField(new StringFieldEditor(PreferenceConstants.PORT.getName(), PreferenceConstants.PORT.getDescription(),
	            getFieldEditorParent()));
		
	}


}
