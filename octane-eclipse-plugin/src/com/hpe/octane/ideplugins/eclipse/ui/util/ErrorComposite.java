package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

/**
 * Composite used to display an error message
 */
public class ErrorComposite extends Composite {

    private Label lblError;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ErrorComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        lblError = new Label(this, SWT.NONE);
        lblError.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
        lblError.setAlignment(SWT.CENTER);
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setErrorMessage(String errorMessage) {
        lblError.setText(errorMessage);
    }

}