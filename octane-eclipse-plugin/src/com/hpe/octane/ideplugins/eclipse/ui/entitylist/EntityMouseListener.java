package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import org.eclipse.swt.events.MouseEvent;

import com.hpe.adm.nga.sdk.model.EntityModel;

public interface EntityMouseListener {
    public void mouseClick(EntityModel entityModel, MouseEvent e);
}
