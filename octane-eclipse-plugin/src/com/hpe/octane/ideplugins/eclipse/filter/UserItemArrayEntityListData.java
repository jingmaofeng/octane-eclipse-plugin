package com.hpe.octane.ideplugins.eclipse.filter;

import java.util.Set;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;

/**
 * Util class for client side filtering of user item entities
 */
public class UserItemArrayEntityListData extends ArrayEntityListData {

    @Override
    public boolean matchStringFilter(EntityModel entityModel, String queryString, Set<String> affectedFields) {
        entityModel = MyWorkUtil.getEntityModelFromUserItem(entityModel);
        return super.matchStringFilter(entityModel, queryString, affectedFields);
    }

    @Override
    public boolean matchTypeFilter(EntityModel entityModel, Set<Entity> entityTypeFilter) {
        entityModel = MyWorkUtil.getEntityModelFromUserItem(entityModel);
        return super.matchTypeFilter(entityModel, entityTypeFilter);
    }

}