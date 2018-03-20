package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
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
        StackLayoutComposite stackLayoutComposite = new StackLayoutComposite(this, SWT.NONE);
        
        @SuppressWarnings("rawtypes")
        FieldModel fieldModel = entityModel.getValue(fieldName);
        Entity entityType = Entity.getEntityType(entityModel);
        FieldMetadata fieldMetadata = metadataService.getMetadata(entityType, fieldName);
     
        String fieldValueString = "";
        
        if (EntityFieldsConstants.FIELD_OWNER.equals(fieldName)
                || EntityFieldsConstants.FIELD_AUTHOR.equals(fieldName)
                || EntityFieldsConstants.FIELD_TEST_RUN_RUN_BY.equals(fieldName)
                || EntityFieldsConstants.FIELD_DETECTEDBY.equals(fieldName)) {
            
            fieldValueString = Util.getUiDataFromModel(fieldModel, EntityFieldsConstants.FIELD_FULL_NAME);
        } else {
            fieldValueString = Util.getUiDataFromModel(entityModel.getValue(fieldName));
        }
        
        FieldValueLabel lblFieldValueText = new FieldValueLabel(stackLayoutComposite, SWT.NONE);
        lblFieldValueText.setText(fieldValueString);
        stackLayoutComposite.showControl(lblFieldValueText);
                
        Label lblEditor = new Label(stackLayoutComposite, SWT.NONE);
        
        lblFieldValueText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                stackLayoutComposite.showControl(lblEditor);
                lblEditor.forceFocus();
            }
        });
        
        lblEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                stackLayoutComposite.showControl(lblFieldValueText);
            }
        });
        
    }

}