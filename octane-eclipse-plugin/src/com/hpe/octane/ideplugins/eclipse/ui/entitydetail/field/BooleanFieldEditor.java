package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.BooleanFieldModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public class BooleanFieldEditor extends Composite implements FieldEditor {
    
    protected EntityModelWrapper entityModelWrapper;
    protected String fieldName;
    private FieldMessageComposite fieldMessageComposite;
    private ModifyListener modifyListener;
    private Combo combo;

    public BooleanFieldEditor(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        
        combo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.setItems(new String[] {Boolean.TRUE.toString(), Boolean.FALSE.toString()});
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        
        fieldMessageComposite = new FieldMessageComposite(this, SWT.NONE);
        fieldMessageComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = combo.getText();
                Boolean bool = Boolean.parseBoolean(text);
                entityModelWrapper.setValue(new BooleanFieldModel(fieldName, bool)); 
            }
        };
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        this.entityModelWrapper = entityModel;
        this.fieldName = fieldName;
        combo.removeModifyListener(modifyListener);
        Boolean boolValue = (Boolean) entityModel.getValue(fieldName).getValue();
        combo.setText(boolValue.toString());     
        combo.addModifyListener(modifyListener);
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