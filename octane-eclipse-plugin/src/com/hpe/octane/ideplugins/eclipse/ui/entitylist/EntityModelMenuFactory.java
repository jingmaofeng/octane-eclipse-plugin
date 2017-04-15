package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.hpe.adm.nga.sdk.model.EntityModel;

public interface EntityModelMenuFactory {
    public Menu createMenu(EntityModel entityModel, Control menuParent);
}