package com.hpe.octane.ideplugins.eclipse.ui.search;

import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_DESCRIPTION;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRenderer;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class SearchResultRowRenderer implements EntityModelRenderer {

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(40, 40, 14);

    @Override
    public Control createRow(Composite parent, EntityModel entityModel) {

        int entityId = Integer.valueOf(Util.getUiDataFromModel(entityModel.getValue("id")));
        Entity entityType = Entity.getEntityType(entityModel);

        EntityModelRow row = new EntityModelRow(parent, SWT.NONE);
        row.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        row.setBackgroundMode(SWT.INHERIT_FORCE);

        row.setEntityId(entityId);
        row.setEntityIcon(entityIconFactory.getImageIcon(entityType));

        String name = Util.getUiDataFromModel(entityModel.getValue("name"));
        name = Util.stripHtml(name);
        row.setEntityName(name);

        String description = Util.getUiDataFromModel(entityModel.getValue(FIELD_DESCRIPTION));
        description = Util.stripHtml(description);
        description = Util.ellipsisTruncate(description, 100); // magic!
        row.setEntitySubTitle(description);

        return row;
    }

}