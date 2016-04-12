package com.example.testplugin.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

public class DefectView extends ViewPart {
	
	public DefectView() {		
	}
	
	public static final String ID = "com.example.testplugin.views.DefectView";
	private TableViewer viewer;
	private Table table;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
