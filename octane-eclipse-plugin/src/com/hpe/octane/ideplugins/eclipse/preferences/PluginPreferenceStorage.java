package com.hpe.octane.ideplugins.eclipse.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.DefaultEntityFieldsUtil;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;

public class PluginPreferenceStorage {

    public interface PrefereceChangeHandler {
        void preferenceChanged();
    }

    public interface PreferenceConstants {
        public static final String OCTANE_SERVER_URL = "octaneServerUrl";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String ACTIVE_ITEM_ID = "ACTIVE_ITEM_ID";
        public static final String ACTIVE_ITEM_ENTITY = "ACTIVE_ITEM_ENTITY";
        public static final String ACTIVE_ITEM_TITLE = "ACTIVE_ITEM_TITLE";
        public static final String SHOWN_ENTITY_FIELDS = "SHOWN_ENTITY_FIELDS";
    }

    private static Map<String, List<PrefereceChangeHandler>> prefereceChangeHandlerMap = new HashMap<>();

    public static void setActiveItem(EntityModelEditorInput entityModelEditorInput) {
        ISecurePreferences securePrefs = getSecurePrefs();

        if (entityModelEditorInput == null) {
            securePrefs.remove(PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_ID);
            securePrefs.remove(PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_ENTITY);
            securePrefs.remove(PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_TITLE);
        } else {
            try {
                securePrefs.putLong(
                        PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_ID,
                        entityModelEditorInput.getId(),
                        false);
                securePrefs.put(
                        PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_ENTITY,
                        entityModelEditorInput.getEntityType().name(),
                        false);
                securePrefs.put(
                        PluginPreferenceStorage.PreferenceConstants.ACTIVE_ITEM_TITLE,
                        entityModelEditorInput.getTitle(),
                        false);

                fireChangeHandler(PreferenceConstants.ACTIVE_ITEM_ID);
                fireChangeHandler(PreferenceConstants.ACTIVE_ITEM_ENTITY);
                fireChangeHandler(PreferenceConstants.ACTIVE_ITEM_TITLE);

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
    }

    public static EntityModelEditorInput getActiveItem() {
        EntityModelEditorInput editorInput;
        ISecurePreferences securePrefs = getSecurePrefs();
        try {
            Long id = securePrefs.getLong(PreferenceConstants.ACTIVE_ITEM_ID, -1);
            Entity entityType = Entity.valueOf(securePrefs.get(PreferenceConstants.ACTIVE_ITEM_ENTITY, Entity.DEFECT.name()));
            String title = securePrefs.get(PreferenceConstants.ACTIVE_ITEM_TITLE, null);
            editorInput = new EntityModelEditorInput(id, entityType, title);
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

    public static void setShownEntityFields(Map<Entity, Set<String>> shownEntityFields) {
        ISecurePreferences securePrefs = getSecurePrefs();
        if (shownEntityFields == null) {
            securePrefs.remove(PreferenceConstants.SHOWN_ENTITY_FIELDS);
        } else {
            try {
                securePrefs.put(PreferenceConstants.SHOWN_ENTITY_FIELDS, DefaultEntityFieldsUtil.entityFieldsToJson(shownEntityFields), false);
                fireChangeHandler(PreferenceConstants.SHOWN_ENTITY_FIELDS);
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
    }

    public static Map<Entity, Set<String>> getShownEntityFields() {
        ISecurePreferences securePrefs = getSecurePrefs();
        String shownEntityFields;
        try {
            shownEntityFields = securePrefs.get(PreferenceConstants.SHOWN_ENTITY_FIELDS, null);

            if (shownEntityFields == null) {
                return DefaultEntityFieldsUtil.getDefaultFields();
            } else {
                return DefaultEntityFieldsUtil.entityFieldsFromJson(shownEntityFields);
            }

        } catch (StorageException e) {

            Activator.getDefault().getLog().log(
                    new Status(
                            Status.ERROR,
                            Activator.PLUGIN_ID,
                            Status.ERROR,
                            "An exception has occured while getting shown entity fields",
                            e));

            throw new RuntimeException(e);
        }
    }

    /**
     * TODO: atoth, don't allow direct access
     * 
     * @return bad thingy
     */
    public static ISecurePreferences getSecurePrefs() {

        QualifiedName qualifiedName = new QualifiedName(Activator.PLUGIN_ID, "workspaceId");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        try {
            String workspaceId = root.getPersistentProperty(qualifiedName);
            if (workspaceId == null) {
                workspaceId = UUID.randomUUID().toString();
                root.setPersistentProperty(qualifiedName, workspaceId);
            }
            return SecurePreferencesFactory.getDefault().node(workspaceId);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(
                    new Status(
                            Status.ERROR,
                            Activator.PLUGIN_ID,
                            Status.ERROR,
                            "An exception has occured when trying to access the Octane connection details", e));

            String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
            return SecurePreferencesFactory.getDefault().node(workspacePath);
        }
    }

    public static void addPrefenceChangeHandler(String preferenceKey, PrefereceChangeHandler prefereceChangeHandler) {
        if (!prefereceChangeHandlerMap.containsKey(preferenceKey)) {
            prefereceChangeHandlerMap.put(preferenceKey, new ArrayList<>());
        }
        prefereceChangeHandlerMap.get(preferenceKey).add(prefereceChangeHandler);
    }

    private static void fireChangeHandler(String preferenceKey) {
        List<PrefereceChangeHandler> handlers = prefereceChangeHandlerMap.get(preferenceKey);
        if (handlers != null) {
            handlers.forEach(prefereceChangedHandler -> prefereceChangedHandler.preferenceChanged());
        }
    }

}
