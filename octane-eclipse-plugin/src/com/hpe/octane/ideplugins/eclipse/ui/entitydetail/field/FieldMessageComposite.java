package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;

import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field.FieldEditor.FieldMessage;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;

public class FieldMessageComposite extends Composite {

    private Label lblMessage;
    private Composite parent;
    private FieldMessage fieldMessage;

    public FieldMessageComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());
        lblMessage = new Label(this, SWT.NONE);
    }

    public void setFieldMessage(FieldMessage fieldMessage) {
        this.fieldMessage = fieldMessage;
        
        if(fieldMessage == null) {
            setFieldMessageLabelVisibile(false);
        } else {
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
    }

    private void setFieldMessageLabelVisibile(boolean isVisible) {
        lblMessage.setVisible(isVisible);
        layout();
        parent.layout();
    }

    public FieldMessage getFieldMessage() {
        return fieldMessage;
    }

}