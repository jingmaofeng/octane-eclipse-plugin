package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolTip;

import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.TruncatingStyledText;

public class ReadOnlyFieldEditor extends Composite implements FieldEditor {

    private TruncatingStyledText lblFieldValue;
    private ToolTip toolTip;

    public ReadOnlyFieldEditor(Composite parent, int style) {
        super(parent, style);
        toolTip = new ToolTip(parent.getShell(), SWT.NONE);
        setLayout(new GridLayout(1, false));
        lblFieldValue = new TruncatingStyledText(this, SWT.READ_ONLY | SWT.SINGLE, toolTip);
        lblFieldValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        lblFieldValue.setText(Util.getUiDataFromModel(entityModel.getValue(fieldName)));
        
        //Removes a bunch of unnecessary listeners 
        if(lblFieldValue.getText().isEmpty()) {
            lblFieldValue.setEnabled(false);
        } else {
            lblFieldValue.setEnabled(true);
        }
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {}

    @Override
    public FieldMessage getFieldMessage() { return null; }
}
