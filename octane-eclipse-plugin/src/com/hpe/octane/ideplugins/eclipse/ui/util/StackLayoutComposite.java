package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Composite that allows you to add many other {@link Control}, and the switch
 * between which one to show
 */
public class StackLayoutComposite extends Composite {

    // private static final ILog logger = Activator.getDefault().getLog();

    private StackLayout layout;
    private Composite parent;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public StackLayoutComposite(Composite parent, int style) {
        super(parent, style);
        this.parent = parent;
        layout = new StackLayout();
        setLayout(layout);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void showControl(Control control) {
        layout.topControl = control;
        // layout of parent works
        parent.layout(true, true);
        redraw();
        update();
    }

    public Control getCurrentControl() {
        return layout.topControl;
    }

}
