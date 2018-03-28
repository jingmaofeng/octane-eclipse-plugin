package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model;

import java.util.Set;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;

@SuppressWarnings("rawtypes")
public class ReadOnlyEntityModel extends EntityModel {
    
    public EntityModel setValue(FieldModel fieldModel) {
        throw new RuntimeException(ReadOnlyEntityModel.class.toString() + " is not read-only");
    }
    
    public EntityModel setValues(Set<FieldModel> values) {
        throw new RuntimeException(ReadOnlyEntityModel.class.toString() + " is not read-only");
    }
    
}