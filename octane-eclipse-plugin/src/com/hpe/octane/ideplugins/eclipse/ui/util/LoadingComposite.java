package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LoadingComposite extends Composite {

    private boolean isLoading = false;
    private GridData loadingGridData;
    private GridData maskedControlGridData;
    private Label lblLoading;
    private Composite composite;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public LoadingComposite(Composite parent, Control maskedControl, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        lblLoading = new Label(this, SWT.NONE);
        lblLoading.setAlignment(SWT.CENTER);
        lblLoading.setText("Loading...");
        loadingGridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);

        // Initially not loading
        loadingGridData.exclude = true;
        lblLoading.setVisible(false);

        lblLoading.setLayoutData(loadingGridData);

        composite = new Composite(this, SWT.BORDER);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        maskedControlGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        composite.setLayoutData(maskedControlGridData);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setIsLoading(boolean isLoading) {
        if (isLoading) {
            loadingGridData.exclude = true;
            lblLoading.setVisible(false);
            maskedControlGridData.exclude = false;
            composite.setVisible(true);
        } else {
            loadingGridData.exclude = false;
            lblLoading.setVisible(true);
            maskedControlGridData.exclude = true;
            composite.setVisible(false);
        }
        getShell().layout(false);
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

}
