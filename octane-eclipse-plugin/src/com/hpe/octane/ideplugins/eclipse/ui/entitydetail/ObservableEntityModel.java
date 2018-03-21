package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;

@SuppressWarnings("rawtypes")
public class ObservableEntityModel extends EntityModel {
    
    public static interface FieldModelChangedHandler {
        public void fieldModelChanged(FieldModel fieldModel);
    }
    
    private List<FieldModelChangedHandler> changeHandlers = new ArrayList<>();
    
    @Override
    public EntityModel setValue(FieldModel fieldModel) {
        EntityModel entityModel = super.setValue(fieldModel);
        callChangeHandlers(fieldModel);
        return entityModel;
    }
    
    @Override
    public EntityModel setValues(Set<FieldModel> values) {
        EntityModel entityModel = super.setValues(values);
        values.forEach(fieldModel -> callChangeHandlers(fieldModel));
        return entityModel;
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