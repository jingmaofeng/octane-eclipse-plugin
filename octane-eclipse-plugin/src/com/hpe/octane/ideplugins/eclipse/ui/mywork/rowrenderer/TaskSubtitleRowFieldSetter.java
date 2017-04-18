package com.hpe.octane.ideplugins.eclipse.ui.mywork.rowrenderer;

import static com.hpe.octane.ideplugins.eclipse.ui.entitylist.DefaultRowEntityFields.getEntityDisplayName;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;

public class TaskSubtitleRowFieldSetter implements RowFieldSetter {

    @Override
    public void setField(EntityModelRow row, EntityModel entityModel) {
        // Add parent details for tasks
        EntityModel storyEntityModel = (EntityModel) entityModel.getValue("story").getValue();

        StringBuilder parentInfoSb = new StringBuilder();
        parentInfoSb.append("Task of ");
        parentInfoSb.append(getEntityDisplayName(Entity.getEntityType(storyEntityModel)).toLowerCase());
        parentInfoSb.append(" " + storyEntityModel.getValue("id").getValue().toString() + ": ");
        parentInfoSb.append(storyEntityModel.getValue("name").getValue().toString());

        row.setEntitySubTitle(parentInfoSb.toString(), "no parent");
    }

    @Override
    public String[] getFieldNames() {
        return new String[] { "story" };
    }

}