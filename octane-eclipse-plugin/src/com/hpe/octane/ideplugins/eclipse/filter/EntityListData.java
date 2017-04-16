package com.hpe.octane.ideplugins.eclipse.filter;

import java.util.Collection;
import java.util.Set;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.filter.ArrayEntityListData.DataChangedHandler;

public interface EntityListData {

    void setEntityList(Collection<EntityModel> entityList);

    Collection<EntityModel> getEntityList();

    void add(EntityModel entityModel);

    void remove(EntityModel entityModel);

    void setTypeFilter(Set<Entity> entityTypeFilter);

    void setStringFilter(String query);

    void setStringFilterFields(Set<String> queryFields);

    boolean matchStringFilter(EntityModel entityModel, String queryString, Set<String> affectedFields);

    boolean matchTypeFilter(EntityModel entityModel, Set<Entity> entityTypeFilter);

    void addDataChangedHandler(DataChangedHandler dataChangedHandler);

    void removeDataChangedHandler(DataChangedHandler dataChangedHandler);

}