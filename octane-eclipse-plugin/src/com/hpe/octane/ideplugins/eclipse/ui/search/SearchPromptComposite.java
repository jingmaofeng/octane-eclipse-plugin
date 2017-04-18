package com.hpe.octane.ideplugins.eclipse.ui.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class SearchPromptComposite extends Composite {

    public SearchPromptComposite(Composite parent, int style, Runnable searchClicked) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        Label lblLogo = new Label(this, SWT.NONE);
        lblLogo.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));
        lblLogo.setImage(ImageResources.OCTANE_LOGO.getImage());

        Label lblPlaceholder = new Label(this, SWT.NONE);

        Link link = new Link(this, SWT.NONE);
        link.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
        link.setText("<a>Search Octane</a>");
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                searchClicked.run();
            }
        });
    }

}