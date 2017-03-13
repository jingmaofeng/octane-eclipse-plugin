package com.hpe.octane.ideplugins.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.provider.IProviderHints;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.hpe.adm.octane.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.HttpClientProvider;
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

    private static BasicConnectionSettingProvider settingsProviderInstance = new BasicConnectionSettingProvider();
    private static ServiceModule serviceModuleInstance = new ServiceModule(settingsProviderInstance);
    public static ISecurePreferences securePrefsInstance = getSecurePrefs();

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

    public static void addConnectionSettingsChangeHandler(Runnable changeHandler) {
        settingsProviderInstance.addChangeHandler(changeHandler);
    }

    public static HttpClientProvider geOctaneHttpClient() {
        return serviceModuleInstance.geOctaneHttpClient();
    }

    public static <T> T getInstance(Class<T> type) {
        return serviceModuleInstance.getInstance(type);
    }

    public static ISecurePreferences getSecurePrefs() {
        String secureStorePath = ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .append(".metadata\\.plugins\\org.eclipse.core.runtime\\.settings\\" + Activator.PLUGIN_ID + "-secure.prefs").toOSString();
        File file = new File(secureStorePath);
        try {
            URL url = file.toURI().toURL();
            Map<String, Object> options = new HashMap<>();
            options.put(IProviderHints.DEFAULT_PASSWORD, new PBEKeySpec("masterpass".toCharArray()));
            return SecurePreferencesFactory.open(url, options);
        } catch (IOException e) {
            getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR,
                    "An exception has occured when fetching secure preferences", e));
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        try {
            ISecurePreferences securePrefs = getSecurePrefs();
            String baseUrl = securePrefs.get(PreferenceConstants.OCTANE_SERVER_URL, "");
            String username = securePrefs.get(PreferenceConstants.USERNAME, "");
            String password = securePrefs.get(PreferenceConstants.PASSWORD, "");
            if (StringUtils.isNotEmpty(baseUrl)) {
                ConnectionSettings loadedConnectionSettings = UrlParser.resolveConnectionSettings(baseUrl, username, password);
                settingsProviderInstance.setConnectionSettings(loadedConnectionSettings);
            } else {
                settingsProviderInstance.setConnectionSettings(new ConnectionSettings());
            }

        } catch (Exception e) {
            getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR,
                    "An exception has occured when loading the Octane connection details", e));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext)
     */
    @Override
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
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     *
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
