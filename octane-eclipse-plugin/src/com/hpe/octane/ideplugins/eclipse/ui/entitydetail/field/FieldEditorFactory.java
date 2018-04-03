package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public class FieldEditorFactory {
    
    private MetadataService metadataService = Activator.getInstance(MetadataService.class);
    
    public FieldEditor createFieldEditor(Composite parent, EntityModelWrapper entityModelWrapper, String fieldName) {
        
        EntityModel entityModel = entityModelWrapper.getReadOnlyEntityModel();
        Entity entityType = Entity.getEntityType(entityModel);
        FieldMetadata fieldMetadata = metadataService.getMetadata(entityType, fieldName);
        
        //String fieldValueString = getFieldValueString(entityModel, fieldName);
        
        FieldEditor fieldEditor = null;
        
        switch(fieldMetadata.getFieldType()){
//            case Integer:
//                break;
//            case Float:
//                break;
//            case Date:
//                break;
//            case DateTime:
//                break;
//            case Boolean:
//                break;
            default:
                fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);
                fieldEditor.setField(entityModelWrapper, fieldName);
                break;
        }
        
        return fieldEditor;
    }
   
}