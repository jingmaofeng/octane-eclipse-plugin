package com.hpe.octane.ideplugins.eclipse.ui.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class NoSearchResultsComposite extends Composite {

    public NoSearchResultsComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        Label lblPlaceholder = new Label(this, SWT.NONE);
        lblPlaceholder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        Label lblUnidragon = new Label(this, SWT.NONE);
        lblUnidragon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
        lblUnidragon.setImage(ImageResources.UNIDRAG_SMALL_SAD.getImage());

        Label lblNoResults = new Label(this, SWT.NONE);
        lblNoResults.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
        lblNoResults.setText("No results");
    }

}