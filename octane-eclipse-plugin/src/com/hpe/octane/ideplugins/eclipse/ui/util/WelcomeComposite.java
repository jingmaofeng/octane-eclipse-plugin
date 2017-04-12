package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import com.hpe.octane.ideplugins.eclipse.util.ResourceManager;

public class WelcomeComposite extends Composite {

    private static final String OCTANE_SETTINGS_TEXT = "To start, go to Settings and connect.";
    private static final String WELCOME_TEXT = "Welcome to ALM Octane plugin";

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public WelcomeComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(3, false));

        Label lblPlaceholder = new Label(this, SWT.NONE);
        lblPlaceholder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1));

        Label lblCompanyLogo = new Label(this, SWT.NONE);
        lblCompanyLogo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
        lblCompanyLogo.setImage(ResourceManager.getPluginImage("octane-eclipse-plugin", "icons/hpe-logo.png"));
        new Label(this, SWT.NONE);

        Label lblProductLogo = new Label(this, SWT.NONE);
        lblProductLogo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
        lblProductLogo.setImage(ResourceManager.getPluginImage("octane-eclipse-plugin", "icons/octane-logo.png"));

        Label lblWelcome = new Label(this, SWT.NONE);
        lblWelcome.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
        lblWelcome.setText(WELCOME_TEXT);

        Link link = new Link(this, SWT.NONE);
        link.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 3, 1));
        link.setText("</a>" + OCTANE_SETTINGS_TEXT + "</a>");
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Selection: " + event.text);
            }
        });
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
