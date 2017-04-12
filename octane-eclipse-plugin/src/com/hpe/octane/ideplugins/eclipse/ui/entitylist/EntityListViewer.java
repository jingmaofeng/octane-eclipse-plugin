package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.Collection;

import org.eclipse.swt.graphics.Drawable;

import com.hpe.adm.nga.sdk.model.EntityModel;

public interface EntityListViewer extends Drawable {

    public void setEntityModels(Collection<EntityModel> entityModels);

    public void addEntityMouseListener(EntityMouseListener entityMouseListener);

    public void removeEntityMouseListener(EntityMouseListener entityMouseListener);

}