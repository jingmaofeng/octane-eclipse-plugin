package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.WelcomeComposite;

/**
 * ViewPart intended for controls for the Octane plugin <br>
 * Will show WelcomeComposite if connection settings is missing. <br>
 * Has support for masking the whole widget with a LoadingComposite
 */
public abstract class OctaneViewPart extends ViewPart {

    private Composite parent;

    private WelcomeComposite welcomeComposite;
    private LoadingComposite loadingComposite;
    private Control octaneViewControl;

    private StackLayoutComposite rootContainer;

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;

        rootContainer = new StackLayoutComposite(parent, SWT.NONE);
        welcomeComposite = new WelcomeComposite(rootContainer, SWT.NONE);
        loadingComposite = new LoadingComposite(rootContainer, SWT.NONE);
        octaneViewControl = createOctanePartControl(rootContainer);

        Activator.addConnectionSettingsChangeHandler(() -> {
            showDefaultView();
        });
    }

    private void showDefaultView() {
        if (Activator.getConnectionSettings().isEmpty()) {
            showWelcome();
        } else {
            showContent();
        }
    }

    public abstract Control createOctanePartControl(Composite octaneControlParent);

    public void showLoading() {
        showControl(loadingComposite);
    }

    public void showContent() {
        showControl(octaneViewControl);
    }

    public void showWelcome() {
        showControl(welcomeComposite);
    }

    private void showControl(Control control) {
        parent.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                rootContainer.showControl(control);

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