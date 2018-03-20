package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;

class FieldValueLabel extends Composite{

    private Label lbl;

    public FieldValueLabel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        setLayout(gridLayout);
        gridLayout.verticalSpacing = 0;
        
        lbl = new Label(this, SWT.NONE);
        lbl.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        lbl.setText("VALUE");
        
        Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setForeground(PlatformResourcesManager.getPlatformForegroundColor());
        label.setBackground(PlatformResourcesManager.getPlatformForegroundColor());
        GridData gd_label = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd_label.heightHint = 1;
        label.setLayoutData(gd_label);
    }
    
    public void setText(String text) {
        lbl.setText(text);
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        lbl.addMouseListener(listener);
    }

}