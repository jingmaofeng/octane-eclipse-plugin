package com.hpe.octane.ideplugins.eclipse.preferences;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hpe.adm.octane.services.TestService;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.util.Constants;
import com.hpe.adm.octane.services.util.UrlParser;
import com.hpe.octane.ideplugins.eclipse.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PluginPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text textServerUrl;
	private Text textSharedSpace;
	private Text textWorkspace;
	private Text textUsername;
	private Text textPassword;
	private Label labelConnectionStatus;

	private IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
	private ISecurePreferences securePrefs = SecurePreferencesFactory.getDefault().node(Activator.PLUGIN_ID);
	private TestService testService = Activator.getServiceModuleInstance().getInstance(TestService.class);

	private String serverUrl, username, password;

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {

		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);

		Label labelServerUrl = new Label(parent, SWT.NONE);
		labelServerUrl.setText("Server URL:");

		textServerUrl = new Text(parent, SWT.BORDER);
		textServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelSharedSpace = new Label(parent, SWT.NONE);
		labelSharedSpace.setText("Shared space:");

		textSharedSpace = new Text(parent, SWT.BORDER);
		textSharedSpace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		textSharedSpace.setEnabled(false);

		Label labelWorkspace = new Label(parent, SWT.NONE);
		labelWorkspace.setText("Workspace:");

		textWorkspace = new Text(parent, SWT.BORDER);
		textWorkspace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		textWorkspace.setEnabled(false);

		Label separator1 = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label labelUsername = new Label(parent, SWT.NONE);
		labelUsername.setText("Username:");

		textUsername = new Text(parent, SWT.BORDER);
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPassword = new Label(parent, SWT.NONE);
		labelPassword.setText("Password:");

		textPassword = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label separator2 = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite testConnectionContainer = new Composite(parent, SWT.NONE);
		testConnectionContainer.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		testConnectionContainer.setLayoutData(gridData);

		Button buttonTestConnection = new Button(testConnectionContainer, SWT.PUSH);
		buttonTestConnection.setText("Test connection");

		labelConnectionStatus = new Label(testConnectionContainer, SWT.NONE);
		labelConnectionStatus.setLayoutData(gridData);

		setHints(true);

		loadSavedValues();

		setFieldsFromServerUrl(false);

		buttonTestConnection.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				setConnectionStatus(null, null);
				cacheValues();
				new Job("Testing connection...") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("Testing connection...", IProgressMonitor.UNKNOWN);
						testConnection(serverUrl, username, password);
						monitor.done();
						return Status.OK_STATUS;
					}
				}.schedule();
			}

		});

		textServerUrl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				setFieldsFromServerUrl(true);
			}
		});

		return parent;
	}

	@Override
	protected void performApply() {
		apply();
	}

	@Override
	public boolean performOk() {
		apply();
		return true;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		textUsername.setText("");
		textPassword.setText("");
		textServerUrl.setText("");
		setFieldsFromServerUrl(false);
		setConnectionStatus(false, "");
	}

	private void setConnectionStatus(Boolean success, String errorMessage) {
		if (labelConnectionStatus.isDisposed())
			return;
		if (success == null) {
			labelConnectionStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			labelConnectionStatus.setText("Testing connection, please wait.");
		} else if (success) {
			labelConnectionStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
			labelConnectionStatus.setText("Connection successful.");
		} else {
			labelConnectionStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			labelConnectionStatus.setText(errorMessage);
		}
		labelConnectionStatus.getParent().requestLayout();
	}

	private void loadSavedValues() {
		textServerUrl.setText(prefs.getString(PreferenceConstants.P_OCTANE_SERVER_URL));
		textUsername.setText(prefs.getString(PreferenceConstants.P_USERNAME));
		try {
			textPassword.setText(securePrefs.get(PreferenceConstants.P_PASSWORD, ""));
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	private void cacheValues() {
		serverUrl = textServerUrl.getText();
		username = textUsername.getText();
		password = textPassword.getText();
	}

	private void saveValues() {
		prefs.putValue(PreferenceConstants.P_OCTANE_SERVER_URL, serverUrl);
		prefs.putValue(PreferenceConstants.P_USERNAME, username);
		try {
			securePrefs.put(PreferenceConstants.P_PASSWORD, password, true);
			securePrefs.flush();
		} catch (StorageException | IOException e) {
			e.printStackTrace();
		}
	}

	private void setHints(boolean forServerUrlField) {
		if (forServerUrlField) {
			textServerUrl.setMessage("Copy paste your Octane URL from the browser here...");
		}
		textSharedSpace.setText("Retrieved from server URL");
		textWorkspace.setText(textSharedSpace.getText());
	}

	private void apply() {
		cacheValues();
		if (isConnectionSettingsEmpty()) {
			Activator.setConnectionSettings(new ConnectionSettings());
			saveValues();
			return;
		}
		Job testConnectionJob = new Job("Applying connection settings...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Applying connection settings...", IProgressMonitor.UNKNOWN);
				ConnectionSettings connectionSettings = testConnection(serverUrl, username, password);
				if (connectionSettings != null) {
					Display.getDefault().asyncExec(() -> {
						if (!textServerUrl.isDisposed()) {
							textServerUrl.setText(UrlParser.createUrlFromConnectionSettings(connectionSettings));
						}
						saveValues();
					});
					Activator.setConnectionSettings(connectionSettings);
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		testConnectionJob.schedule();
	}

	private boolean isConnectionSettingsEmpty() {
		return StringUtils.isEmpty(textServerUrl.getText()) && StringUtils.isEmpty(textUsername.getText())
				&& StringUtils.isEmpty(textPassword.getText());
	}

	private ConnectionSettings testConnection(String serverUrl, String username, String password) {
		ConnectionSettings newConnectionSettings;

		try {
			newConnectionSettings = UrlParser.resolveConnectionSettings(serverUrl, username, password);
		} catch (ServiceException e) {
			Display.getDefault().asyncExec(
					() -> setConnectionStatus(false, e.getMessage() + "\n" + Constants.CORRECT_URL_FORMAT_MESSAGE));
			return null;
		}

		try {
			validateUsernameAndPassword(username, password);
		} catch (ServiceException e) {
			Display.getDefault().asyncExec(() -> setConnectionStatus(false, e.getMessage()));
			return null;
		}

		try {
			testService.testConnection(newConnectionSettings);
			Display.getDefault().asyncExec(() -> setConnectionStatus(true, null));
		} catch (ServiceException e) {
			Display.getDefault().asyncExec(() -> setConnectionStatus(false, e.getMessage()));
			return null;
		}

		return newConnectionSettings;
	}

	private void validateUsernameAndPassword(String username, String password) throws ServiceException {
		StringBuilder errorMessageBuilder = new StringBuilder();
		if (StringUtils.isEmpty(username)) {
			errorMessageBuilder.append("Username cannot be blank.");
		}
		if (errorMessageBuilder.length() != 0) {
			errorMessageBuilder.append(" ");
		}
		if (StringUtils.isEmpty(password)) {
			errorMessageBuilder.append("Password cannot be blank.");
		}

		if (errorMessageBuilder.length() != 0) {
			throw new ServiceException(errorMessageBuilder.toString());
		}
	}

	private void setFieldsFromServerUrl(boolean setStatus) {
		ConnectionSettings connectionSettings;
		try {
			connectionSettings = UrlParser.resolveConnectionSettings(textServerUrl.getText(), textUsername.getText(),
					textPassword.getText());
			textSharedSpace.setText(connectionSettings.getSharedSpaceId() + "");
			textWorkspace.setText(connectionSettings.getWorkspaceId() + "");
			setConnectionStatus(false, "");
		} catch (ServiceException e) {
			setHints(false);
			if (setStatus) {
				setConnectionStatus(false,
						e.getMessage() + "\n" + com.hpe.adm.octane.services.util.Constants.CORRECT_URL_FORMAT_MESSAGE);
			}
		}
	}
}