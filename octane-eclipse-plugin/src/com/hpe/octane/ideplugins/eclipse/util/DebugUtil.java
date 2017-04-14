package com.hpe.octane.ideplugins.eclipse.util;

import java.util.Collection;
import java.util.stream.Collectors;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.ConnectionSettingsProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;

public class DebugUtil {

    private static String OCTANE_URL = "http://myd-vm02524.hpeswlab.net:7521";
    private static Long sharedspace = 1001L;
    private static Long workspace = 2003L;
    private static String USERNAME = "alexandra.marinescu@hpe.com";
    private static String PASSWORD = "Welcome1";

    private static ConnectionSettings connectionSettings = new ConnectionSettings();
    static {
        connectionSettings.setBaseUrl(OCTANE_URL);
        connectionSettings.setSharedSpaceId(sharedspace);
        connectionSettings.setWorkspaceId(workspace);
        connectionSettings.setUserName(USERNAME);
        connectionSettings.setPassword(PASSWORD);
    }

    public static void printEntities(Collection<EntityModel> entities) {
        System.out.println("My Work Entities size: " + entities.size());
        if (entities.size() != 0) {
            String entitiesString = entities
                    .stream()
                    .map(MyWorkUtil::getEntityModelFromUserItem)
                    .map(em -> {
                        if (em.getValue("name") != null) {
                            return em.getValue("name").getValue().toString();
                        } else if (Entity.COMMENT == Entity.getEntityType(em)) {
                            return "(Comment)";
                        }
                        return "{???}";
                    })
                    .collect(Collectors.joining(", "));
            System.out.println("My Work Entities: " + entitiesString);
        }
    }

    public static Collection<EntityModel> getEntitites(Entity entityType) {
        ServiceModule serviceModule = new ServiceModule(new ConnectionSettingsProvider() {
            @Override
            public void setConnectionSettings(ConnectionSettings connectionSettings) {
            }

            @Override
            public ConnectionSettings getConnectionSettings() {
                return connectionSettings;
            }

            @Override
            public void addChangeHandler(Runnable observer) {
            }
        });
        return serviceModule.getInstance(EntityService.class).findEntities(entityType);
    }

}
