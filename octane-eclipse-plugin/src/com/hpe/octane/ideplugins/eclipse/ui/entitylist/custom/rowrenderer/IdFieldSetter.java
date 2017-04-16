package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.rowrenderer;

import static com.hpe.adm.octane.services.util.Util.getUiDataFromModel;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;

class IdFieldSetter implements RowFieldSetter {

    private static String fieldName = "id";

    @Override
    public void setField(EntityModelRow row, EntityModel entityModel) {
        row.setEntityId(Integer.valueOf(getUiDataFromModel(entityModel.getValue(fieldName))));
    }

    @Override
    public String[] getFieldNames() {
        return new String[] { fieldName };
    }
}