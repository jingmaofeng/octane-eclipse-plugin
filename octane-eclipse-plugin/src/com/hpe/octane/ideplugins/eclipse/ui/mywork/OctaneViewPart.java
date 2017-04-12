package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.WelcomeComposite;

/**
 * ViewPart intended for controls for the Octane plugin <br>
 * Will show WelcomeComposite if connection settings is missing. <br>
 * Has support for masking the whole widget with a LoadingComposite
 */
public abstract class OctaneViewPart extends ViewPart {

    private Composite parent;
    protected Composite octaneControlParent;
    private WelcomeComposite welcomeComposite;
    private LoadingComposite loadingComposite;

    private StackLayout rootStackLayout;
    private Composite rootContainer;

    @Override
    public void createPartControl(Composite parent) {

        this.parent = parent;
        rootContainer = new Composite(parent, SWT.NONE);
        rootStackLayout = new StackLayout();
        rootContainer.setLayout(rootStackLayout);

        welcomeComposite = new WelcomeComposite(rootContainer, SWT.NONE);

        loadingComposite = new LoadingComposite(rootContainer, SWT.NONE);

        octaneControlParent = new Composite(rootContainer, SWT.NONE);
        octaneControlParent.setLayout(new FillLayout());
        createOctanePartControl(octaneControlParent);

        Activator.addConnectionSettingsChangeHandler(() -> {
            showDefaultView();
        });

        // Default
        showDefaultView();
    }

    private void showDefaultView() {
        if (Activator.getConnectionSettings().isEmpty()) {
            showWelcome();
        } else {
            showContent();
        }
    }

    public abstract void createOctanePartControl(Composite octaneControlParent);

    public void showLoading() {
        showControl(loadingComposite);
    }

    public void showContent() {
        showControl(octaneControlParent);
    }

    public void showWelcome() {
        showControl(welcomeComposite);
    }

    private void showControl(Control control) {
        parent.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                rootStackLayout.topControl = control;

                // layout of parent works
                parent.layout(true, true);
                // marks the composite's screen are as invalidates, which will
                // force a redraw on next paint request
                rootContainer.redraw();
                // tells the application to do all outstanding paint requests
                // immediately
                rootContainer.update();
            }
        });
    }

}