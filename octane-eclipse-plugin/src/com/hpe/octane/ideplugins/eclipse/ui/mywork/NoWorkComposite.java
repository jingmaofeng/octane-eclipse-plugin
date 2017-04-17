package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class NoWorkComposite extends Composite {

    private static final String NO_WORK_TEXT = "You're Awesome! You finished all your work!";
    private static final String NO_WORK_LINK_TEXT = "You may want to talk with your team leader... or have some fun!";
    private static final Image unidragonImage = ImageResources.UNIDRAG_SMALL.getImage();

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public NoWorkComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        Label lblUnidragon = new Label(this, SWT.NONE);
        lblUnidragon.setImage(unidragonImage);
        lblUnidragon.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));

        Label lblMessage = new Label(this, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        lblMessage.setText(NO_WORK_TEXT);

        Label lblLink = new Label(this, SWT.NONE);
        lblLink.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1));
        lblLink.setText(NO_WORK_LINK_TEXT);
    }

    public void setLinkClickedRunnable(Runnable linkClicked) {

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
