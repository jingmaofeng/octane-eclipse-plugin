package com.hpe.octane.ideplugins.eclipse.ui.search;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class TextContributionItem extends ControlContribution {

    private Text text;

    public TextContributionItem(String id) {
        super(id);
    }

    @Override
    protected Control createControl(Composite parent) {
        ToolBar toolbar = (ToolBar) parent;

        // Force height
        ToolItem ti = new ToolItem(toolbar, SWT.PUSH);
        ti.setImage(createForceHeightImageData());

        text = createText(parent);
        return text;
    }

    protected abstract Text createText(Composite parent);

    @Override
    public int computeWidth(Control control) {
        return 150;
    }

    public Text getTextControl() {
        return text;
    }

    private static Image createForceHeightImageData() {
        Image src = new Image(Display.getCurrent(), 16, 16);
        return src;
    }
}