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
        
        FieldEditor fieldEditor = null;
        
        if(!fieldMetadata.isEditable()) {
            fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);
            
        } else {
            switch(fieldMetadata.getFieldType()){
                case Integer:
                    fieldEditor = new NumericFieldEditor(parent, SWT.NONE, false);
                    ((NumericFieldEditor) fieldEditor).setBounds(0, Long.MAX_VALUE);
                    break;
                case Float:
                    fieldEditor = new NumericFieldEditor(parent, SWT.NONE, true);
                    break;
                case String:
                    fieldEditor = new StringFieldEditor(parent, SWT.NONE);
                    break;
                case Boolean:
                    fieldEditor = new BooleanFieldEditor(parent, SWT.NONE);
                    break;
                case DateTime:
                    fieldEditor = new DateTimeFieldEditor(parent, SWT.NONE);
                    break;
                default:
                    fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);
                    break;
            }
            
        }
        
        fieldEditor.setField(entityModelWrapper, fieldName);
        return fieldEditor;
    }
   
}