package com.hpe.octane.ideplugins.eclipse;

import java.util.Collection;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.ConnectionSettingsProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;

public class TemporaryDataProvider {

    static EntityService service;
    static {
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setBaseUrl("http://myd-vm24085.hpeswlab.net:8080");
        connectionSettings.setSharedSpaceId(1001L);
        connectionSettings.setWorkspaceId(1002L);
        connectionSettings.setUserName("sa@nga");
        connectionSettings.setPassword("Welcome1");
        ConnectionSettingsProvider connectionSettingsProvider = new BasicConnectionSettingProvider(connectionSettings);
        service = new ServiceModule(connectionSettingsProvider).getInstance(EntityService.class);
    }

    public static Collection<EntityModel> findEntities(Entity entityType) {
        return service.findEntities(entityType);
    }

    public static EntityModel findEntity(Entity entityType, Long entityId) throws ServiceException {
        return service.findEntity(entityType, entityId);
    }

}