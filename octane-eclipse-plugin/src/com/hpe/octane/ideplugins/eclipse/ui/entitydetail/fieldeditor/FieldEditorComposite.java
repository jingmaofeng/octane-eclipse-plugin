package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.metadata.FieldMetadata.FieldType;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class FieldEditorComposite extends Composite {
    
    private MetadataService metadataService = Activator.getInstance(MetadataService.class);
    
    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public FieldEditorComposite(Composite parent, int style, EntityModel entityModel, String fieldName) {
        super(parent, style);
        setLayout(new FillLayout(SWT.HORIZONTAL));   
        
        Entity entityType = Entity.getEntityType(entityModel);
        FieldMetadata fieldMetadata = metadataService.getMetadata(entityType, fieldName);
        String fieldValueString = getFieldValueString(entityModel, fieldName);
        
        if(FieldType.Integer.equals(fieldMetadata.getFieldType())) {
            Text editor = new Text(this, SWT.NONE);
            editor.setText(fieldValueString);
        } else {
            FieldValueLabel lbl = new FieldValueLabel(this, SWT.NONE);
            lbl.setText(fieldValueString);
        }
        
    }
    
    private String getFieldValueString(EntityModel entityModel, String fieldName) {
        @SuppressWarnings("rawtypes")
        FieldModel fieldModel = entityModel.getValue(fieldName);
        if (EntityFieldsConstants.FIELD_OWNER.equals(fieldName)
                || EntityFieldsConstants.FIELD_AUTHOR.equals(fieldName)
                || EntityFieldsConstants.FIELD_TEST_RUN_RUN_BY.equals(fieldName)
                || EntityFieldsConstants.FIELD_DETECTEDBY.equals(fieldName)) {
            
            return Util.getUiDataFromModel(fieldModel, EntityFieldsConstants.FIELD_FULL_NAME);
        } else {
            return Util.getUiDataFromModel(entityModel.getValue(fieldName));
        }
    }

}