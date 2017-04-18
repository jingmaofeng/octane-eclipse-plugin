package com.hpe.octane.ideplugins.eclipse.ui.mywork.rowrenderer;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;

class IconFieldSetter implements RowFieldSetter {

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(40, 40, 14);

    @Override
    public void setField(EntityModelRow row, EntityModel entityModel) {
        row.setEntityIcon(entityIconFactory.getImageIcon(Entity.getEntityType(entityModel)));
    }

    @Override
    public String[] getFieldNames() {
        return new String[] {};
    }
}