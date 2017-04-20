package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class SeparatorControlContribution extends ControlContribution {

    public SeparatorControlContribution(String id) {
        super(id);
    }

    @Override
    protected Control createControl(Composite parent) {
        return new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
    }

    @Override
    public int computeWidth(Control control) {
        return 30;
    }

}