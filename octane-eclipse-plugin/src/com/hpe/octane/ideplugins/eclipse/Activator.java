package com.hpe.octane.ideplugins.eclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.hpe.adm.octane.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.HttpClientProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.util.UrlParser;
import com.hpe.octane.ideplugins.eclipse.preferences.PreferenceConstants;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;

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
    private static List<Runnable> activeItemChangedHandler = new ArrayList<Runnable>();

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

    public static void setActiveItem(EntityModelEditorInput entityModelEditorInput) {
        ISecurePreferences securePrefs = getSecurePrefs();

        if (entityModelEditorInput == null) {
            securePrefs.remove(PreferenceConstants.ACTIVE_ITEM_ID);
            securePrefs.remove(PreferenceConstants.ACTIVE_ITEM_ENTITY);
        } else {
            try {
                securePrefs.putLong(
                        PreferenceConstants.ACTIVE_ITEM_ID,
                        entityModelEditorInput.getId(),
                        false);
                securePrefs.put(
                        PreferenceConstants.ACTIVE_ITEM_ENTITY,
                        entityModelEditorInput.getEntityType().name(),
                        false);
            } catch (StorageException e) {
                Activator.getDefault().getLog().log(
                        new Status(
                                Status.ERROR,
                                Activator.PLUGIN_ID,
                                Status.ERROR,
                                "An exception has occured while saving active item",
                                e));
            }
        }
        activeItemChangedHandler.forEach(runnable -> runnable.run());
    }

    public static EntityModelEditorInput getActiveItem() {
        EntityModelEditorInput editorInput = null;
        ISecurePreferences securePrefs = getSecurePrefs();
        try {
            Long id = securePrefs.getLong(PreferenceConstants.ACTIVE_ITEM_ID, -1);
            Entity entityType = Entity.valueOf(securePrefs.get(PreferenceConstants.ACTIVE_ITEM_ENTITY, Entity.DEFECT.name()));
            editorInput = new EntityModelEditorInput(id, entityType);
        } catch (Exception ex) {
            Activator.getDefault().getLog().log(
                    new Status(
                            Status.ERROR,
                            Activator.PLUGIN_ID,
                            Status.ERROR,
                            "An exception has occured while fetching active item",
                            ex));
            return null;
        }

        return (editorInput.getId() != -1) ? editorInput : null;
    }

    public static void addActiveItemChangedHandler(Runnable activeItemChanged) {
        activeItemChangedHandler.add(activeItemChanged);
    }

    public static ISecurePreferences getSecurePrefs() {

        QualifiedName qualifiedName = new QualifiedName(PLUGIN_ID, "workspaceId");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        try {
            String workspaceId = root.getPersistentProperty(qualifiedName);
            if (workspaceId == null) {
                workspaceId = UUID.randomUUID().toString();
                root.setPersistentProperty(qualifiedName, workspaceId);
            }
            return SecurePreferencesFactory.getDefault().node(workspaceId);
        } catch (CoreException e) {
            getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR,
                    "An exception has occured when trying to access the Octane connection details", e));
            String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
            return SecurePreferencesFactory.getDefault().node(workspacePath);
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

        settingsProviderInstance.addChangeHandler(() -> {
            setActiveItem(null);
        });
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
