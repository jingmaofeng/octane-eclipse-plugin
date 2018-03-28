package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class SimpleFieldEditor extends Composite implements FieldEditor {
    
    private EntityModelWrapper entityModel;
    private String fieldName;
    
    private Text textField;
    private Label lblMessage;

    public SimpleFieldEditor(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        
        textField = new Text(this, SWT.BORDER);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        lblMessage = new Label(this, SWT.NONE);
        GridData gd_lblMessage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblMessage.exclude = true;
        lblMessage.setLayoutData(gd_lblMessage);
        
        textField.addModifyListener(e -> {
            
        });
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String... fieldNames) {
        this.entityModel = entityModel;
        this.fieldName = fieldNames[0];
        
        
        
    }

    @Override
    public void forceUpdate() {
        
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {
        switch(fieldMessage.getFieldMessageLevel()) {
            case ERROR:
                lblMessage.setImage(PlatformResourcesManager.getPlatformImage(ISharedImages.IMG_DEC_FIELD_ERROR));
                setFieldMessageLabelVisibile(true);
                break;
            case INFO:
                lblMessage.setImage(PlatformResourcesManager.getPlatformImage(ISharedImages.IMG_OBJS_INFO_TSK));
                setFieldMessageLabelVisibile(true);
                break;
            default:
                setFieldMessageLabelVisibile(false);
                break;
        }
        
        lblMessage.setToolTipText(fieldMessage.getMessage());
    }
    
    private void setFieldMessageLabelVisibile(boolean isVisible) {
          GridData gridData = (GridData) lblMessage.getData();
          gridData.exclude = !isVisible;
          lblMessage.setVisible(isVisible);
          layout();
    }

    @Override
    public void getFieldMessage(FieldMessage fieldMessage) {
        // TODO Auto-generated method stub
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