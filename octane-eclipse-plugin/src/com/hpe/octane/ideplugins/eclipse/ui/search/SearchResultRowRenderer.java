package com.hpe.octane.ideplugins.eclipse.ui.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRenderer;

class SearchResultRowRenderer implements EntityModelRenderer {

    @Override
    public Control createRow(Composite parent, EntityModel entityModel) {
        Label lbl = new Label(parent, SWT.NONE);
        lbl.setText(entityModel.toString());
        return lbl;
    }

}