package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;

@SuppressWarnings("rawtypes")
public class EntityModelWrapper {
    
    private EntityModel entityModel;
    private List<FieldModelChangedHandler> changeHandlers = new ArrayList<>();
    
    public static interface FieldModelChangedHandler {
        public void fieldModelChanged(FieldModel fieldModel);
    }
    
    public EntityModelWrapper(EntityModel entityModel) {
        this.entityModel = entityModel;
    }
    
    public void setValue(FieldModel fieldModel) {
        entityModel.setValue(fieldModel);
        callChangeHandlers(fieldModel);
    }
    
    public void setValues(Set<FieldModel> values) {
        entityModel.setValues(values);
        values.forEach(fieldModel -> callChangeHandlers(fieldModel));
    }
    
    private void callChangeHandlers(FieldModel fieldModel) {
        changeHandlers.forEach(handler -> handler.fieldModelChanged(fieldModel));
    }
    
    public boolean addFieldModelChangedHandler(FieldModelChangedHandler fieldModelChangedHandler) {
        return changeHandlers.add(fieldModelChangedHandler);
    }
    
    public boolean removeFieldModelChangedHandler(FieldModelChangedHandler fieldModelChangedHandler) {
        return changeHandlers.remove(fieldModelChangedHandler);
    }
    
}
