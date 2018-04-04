package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.model.ReferenceErrorModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public class StringFieldEditor extends Composite implements FieldEditor {
    
    protected EntityModelWrapper entityModelWrapper;
    protected String fieldName;
    protected Text textField;
    private FieldMessageComposite fieldMessageComposite;
    private ModifyListener modifyListener;

    public StringFieldEditor(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        
        textField = new Text(this, SWT.BORDER);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        fieldMessageComposite = new FieldMessageComposite(this, SWT.NONE);
        GridData gd_lblMessage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        fieldMessageComposite.setLayoutData(gd_lblMessage);
        
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = textField.getText();
                //whitespace is considered null
                if(text.isEmpty() && text.length() != 0) {
                    entityModelWrapper.setValue(new ReferenceErrorModel(fieldName, null)); 
                } else {
                    entityModelWrapper.setValue(new StringFieldModel(fieldName, text));
                }
            }
        };
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        this.entityModelWrapper = entityModel;
        this.fieldName = fieldName;
        textField.removeModifyListener(modifyListener);
        textField.setText(Util.getUiDataFromModel(entityModel.getValue(fieldName)));
        textField.addModifyListener(modifyListener);
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {
        fieldMessageComposite.setFieldMessage(fieldMessage);
    }
    
    @Override
    public FieldMessage getFieldMessage() {
        return fieldMessageComposite.getFieldMessage();
    }
    
}