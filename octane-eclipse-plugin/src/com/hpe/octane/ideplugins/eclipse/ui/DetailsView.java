package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class DetailsView extends Composite {

	public DetailsView(Composite parent, int style) {
		super(parent, style);
		ExpandableComposite composite = new ExpandableComposite(parent, style);
	}

}
