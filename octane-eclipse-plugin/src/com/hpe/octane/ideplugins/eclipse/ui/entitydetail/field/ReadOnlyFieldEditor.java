package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public class ReadOnlyFieldEditor extends Composite implements FieldEditor {

    private Label lblFieldValue;

    public ReadOnlyFieldEditor(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout(SWT.HORIZONTAL));
        lblFieldValue = new Label(this, SWT.NONE);
        lblFieldValue.setEnabled(false);
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        lblFieldValue.setText(Util.getUiDataFromModel(entityModel.getValue(fieldName)));
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {}

    @Override
    public FieldMessage getFieldMessage() { return null; }
}
