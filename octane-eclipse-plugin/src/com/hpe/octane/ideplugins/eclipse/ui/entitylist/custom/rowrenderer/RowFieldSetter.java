package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.rowrenderer;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;

interface RowFieldSetter {
    public void setField(EntityModelRow row, EntityModel entityModel);

    /**
     * Get the name of the field this setter is adding to the row
     * 
     * @return
     */
    public String[] getFieldNames();
}