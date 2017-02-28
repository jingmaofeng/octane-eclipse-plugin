package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.hpe.adm.octane.services.filtering.Entity;

public class EntityModelEditorInput implements IEditorInput {

    private final long   id;
    private final Entity entityType;

    public EntityModelEditorInput(long id, Entity entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    public long getId() {
        return id;
    }

    public Entity getEntityType() {
        return entityType;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return entityType.name() + " " + id;
    }

    @Override
    public IPersistableElement getPersistable() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getToolTipText() {
        // TODO Auto-generated method stub
        return null;
    }

}