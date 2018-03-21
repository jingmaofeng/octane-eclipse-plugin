package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.ObservableEntityModel;

public class TextEditor extends Composite implements FieldEditor {
    
    private Text textField;
    private ObservableEntityModel entityModel;

    public TextEditor(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        
        textField = new Text(this, SWT.BORDER);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblError = new Label(this, SWT.NONE);
        lblError.setText("ERROR");
        lblError.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
    }

    @Override
    public void setField(ObservableEntityModel entityModel, String fieldName) {
        this.entityModel = entityModel;
    }

    @Override
    public void forceUpdate() {
        
    }

    @Override
    public boolean hasError() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getErrorText() {
        // TODO Auto-generated method stub
        return null;
    }
    
}