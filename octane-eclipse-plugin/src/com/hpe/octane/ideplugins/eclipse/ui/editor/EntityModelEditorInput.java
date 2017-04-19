package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;

public class EntityModelEditorInput implements IEditorInput {

    private final long id;
    private final Entity entityType;

    public EntityModelEditorInput(EntityModel entityModel) {
        this.id = Long.parseLong(entityModel.getValue("id").getValue().toString());
        this.entityType = Entity.getEntityType(entityModel);
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityModelEditorInput other = (EntityModelEditorInput) obj;
        if (entityType != other.entityType)
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EntityModelEditorInput [id=" + id + ", entityType=" + entityType + "]";
    }

}