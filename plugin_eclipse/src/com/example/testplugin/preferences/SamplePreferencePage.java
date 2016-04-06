package com.example.testplugin.preferences;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.example.testplugin.views.ApplRestClientTest;
import com.example.testplugin.views.SecureStorage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SamplePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Text serverUrl;
	private Text userName;
	private Text password;
	private Text workSpace;
	private Text response;
	private String tempServerUrl = "";
	private String tempUserName = "";
	private String tempPassword = "";
	private boolean itWasAtest = false;
	private SecureStorage secureStorage;
	private Button savePasswordButton;

	public SamplePreferencePage() throws StorageException {
		super();
		noDefaultButton();
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.secureStorage = SecureStorage.getInstance();
	}

	private boolean isSecureStorageUsed() {
		return secureStorage.getState() != null;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite preferencesComposite = new Composite(parent, 0);
		GridLayout gridLayout = new GridLayout();
		Group sourceControlGroup = new Group(preferencesComposite, SWT.SHADOW_ETCHED_IN);
		// sourceControlGroup.setLayout(new GridLayout(1, false));
		GridData gd_sourceControlGroup = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		sourceControlGroup.setLayoutData(gd_sourceControlGroup);
		sourceControlGroup.setLayout(gridLayout);
		preferencesComposite.setLayout(gridLayout);
		
		tempServerUrl = secureStorage.getLocation();
		tempUserName = secureStorage.getUserName();
		tempPassword = secureStorage.getPassword();

		serverUrlFields(sourceControlGroup);
		workSpaseFields(sourceControlGroup);
		userNameFields(sourceControlGroup);
		passwordFields(sourceControlGroup);
		responceField(sourceControlGroup);
		buttonTest(sourceControlGroup);
		return sourceControlGroup;
	}

	private void buttonTest(Group sourceControlGroup) {
		Button btnTest = new Button(sourceControlGroup, SWT.NONE);
		GridData gd_btnTest = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnTest.widthHint = 68;
		btnTest.setLayoutData(gd_btnTest);
		btnTest.setToolTipText("Check connection to the server");
		btnTest.setText("Test");
		btnTest.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!itWasAtest) {
					secureStorage.setLocation(serverUrl.getText());
					secureStorage.setUserName(userName.getText());
					secureStorage.setPassword(password.getText());
					itWasAtest = true;
				}		
				switch (event.type) {
				case SWT.Selection:
					String[] responseFromRest = ApplRestClientTest.getRestClient(serverUrl.getText());
					if (responseFromRest[0].equals("200")) {
						response.setText("Connected");
						workSpace.setText(responseFromRest[1]);
					} else {
						response.setText("Failed");
					}
				}
			}
		});
	}

	private void responceField(Group sourceControlGroup) {
		response = new Text(sourceControlGroup, SWT.NONE);
		response.setToolTipText("Status connection");
		response.setEditable(false);
		GridData gd_response = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_response.widthHint = 200;
		response.setLayoutData(gd_response);

		savePasswordButton = new Button(sourceControlGroup, SWT.CHECK);
		savePasswordButton.setToolTipText("To save password");
		savePasswordButton.setText("Save password");
		if (secureStorage.getPassword() == null || secureStorage.getPassword().length() == 0) {
			savePasswordButton.setSelection(false);
		} else {
			savePasswordButton.setSelection(true);
		}
		savePasswordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}

	private void passwordFields(Group sourceControlGroup) {
		Label lblPassword = new Label(sourceControlGroup, SWT.NONE);
		lblPassword.setText("Password");
		password = new Text(sourceControlGroup, SWT.PASSWORD | SWT.BORDER);
		password.setToolTipText("Enter password");
		GridData gd_password = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_password.widthHint = 200;
		password.setLayoutData(gd_password);
		if (isSecureStorageUsed()) {
			password.setText(secureStorage.getPassword());
		} else {
			password.setText("");
		}
	}

	private void userNameFields(Group sourceControlGroup) {
		Label lblUserName = new Label(sourceControlGroup, SWT.NONE);
		lblUserName.setText("User");
		userName = new Text(sourceControlGroup, SWT.BORDER);
		userName.setToolTipText("Enter user name");
		GridData gd_userName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_userName.widthHint = 195;
		userName.setLayoutData(gd_userName);
		if (isSecureStorageUsed()) {
			userName.setText(secureStorage.getUserName());
		} else {
			userName.setText("");
		}
	}

	private void workSpaseFields(Group sourceControlGroup) {
		Label lblWorkSpace = new Label(sourceControlGroup, SWT.NONE);
		lblWorkSpace.setText("WorkSpace");
		workSpace = new Text(sourceControlGroup, SWT.BORDER);
		workSpace.setToolTipText("myShareSpace/myWorkSpace");
		workSpace.setEditable(false);
		GridData gd_WorkSpace = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_WorkSpace.widthHint = 200;
		workSpace.setLayoutData(gd_WorkSpace);
	}

	private void serverUrlFields(Group sourceControlGroup) {
		Label lblServerUrl = new Label(sourceControlGroup, SWT.NONE);
		lblServerUrl.setText("Server URL");
		serverUrl = new Text(sourceControlGroup, SWT.BORDER);
		serverUrl.setToolTipText("Enter URL to the server");
		serverUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (isSecureStorageUsed()) {
			serverUrl.setText(secureStorage.getLocation());
		} else {
			serverUrl.setText("");
		}
	}

	public boolean performCancel() {
		if (itWasAtest) {
			secureStorage.setLocation(tempServerUrl);
			secureStorage.setUserName(tempUserName);
			secureStorage.setPassword(tempPassword);
			secureStorage.setState(true);
		}
		return super.performCancel();
	}

	public boolean performOk() {
		if (!itWasAtest) {
			secureStorage.setLocation(serverUrl.getText());
			secureStorage.setUserName(userName.getText());
			if (savePasswordButton.getSelection()) {
				secureStorage.setPassword(password.getText());
			} else {
				secureStorage.setPassword("");
			}
			secureStorage.setState(true);
			itWasAtest = true;
		}
		return super.performOk();
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}