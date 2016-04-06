package com.example.testplugin.views;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class DialogPreferencePage {

	public static void preferenceDialog(String errorMessage) {
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null,
				"com.example.testplugin.preferences.SamplePreferencePage",
				new String[] { "com.example.testplugin.preferences.SamplePreferencePage" }, null);
		if (errorMessage != "") {
			dialog.setErrorMessage(errorMessage);
		}
		dialog.open();
	}

}
