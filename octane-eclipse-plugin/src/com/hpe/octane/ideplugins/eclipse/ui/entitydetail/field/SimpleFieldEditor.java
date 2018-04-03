package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;

import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;

public abstract class SimpleFieldEditor extends Composite implements FieldEditor {
    
    protected EntityModelWrapper entityModel;
    protected String fieldName;
    protected Text textField;
    
    private ModifyListener modifyListener;
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
        

        textField.addModifyListener(createModifyListener());
        textField.setText(getFieldValueString());
    }
    
    protected abstract ModifyListener createModifyListener();
    protected abstract String getFieldValueString();

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        this.entityModel = entityModel;
        this.fieldName = fieldName;
        
        if(modifyListener != null) {
            textField.removeModifyListener(modifyListener);
        }
        modifyListener = createModifyListener();
        textField.setText(getFieldValueString());
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
    
}