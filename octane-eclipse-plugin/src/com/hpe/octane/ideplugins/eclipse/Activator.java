package com.hpe.octane.ideplugins.eclipse;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.hpe.adm.octane.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.util.UrlParser;
import com.hpe.octane.ideplugins.eclipse.preferences.PreferenceConstants;
   
/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "octane-eclipse-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private static ServiceModule serviceModuleInstance;
	private static BasicConnectionSettingProvider settingsProviderInstance = new BasicConnectionSettingProvider();
	
	/**
	 * The constructor
	 */
	public Activator() {
	}


	public static void setConnectionSettings(ConnectionSettings connectionSettings) {
		settingsProviderInstance.setConnectionSettings(connectionSettings);
	}

	public static ConnectionSettings getConnectionSettings() {
		return settingsProviderInstance.getConnectionSettings();
	}

	public static ServiceModule getServiceModuleInstance() {
		if (serviceModuleInstance == null) {
			serviceModuleInstance = new ServiceModule(settingsProviderInstance);
		}
		return serviceModuleInstance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IPreferenceStore prefs = getPreferenceStore();
		ISecurePreferences securePrefs = SecurePreferencesFactory.getDefault().node(Activator.PLUGIN_ID);
		
		try {
			String baseUrl = prefs.getString(PreferenceConstants.P_OCTANE_SERVER_URL);
			String username = prefs.getString(PreferenceConstants.P_USERNAME);
			String password = securePrefs.get(PreferenceConstants.P_PASSWORD, "");
			ConnectionSettings loadedConnectionSettings = UrlParser.resolveConnectionSettings(baseUrl, username, password);
			settingsProviderInstance.setConnectionSettings(loadedConnectionSettings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
