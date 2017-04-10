package com.hpe.octane.ideplugins.eclipse.ui.entitylist.treetable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;

public class TreeTableEntityListViewer extends Composite implements EntityListViewer {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TreeTableEntityListViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		entityList = listViewer.getList();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private Map<Entity, Collection<EntityModel>> groupedEntitites = new LinkedHashMap<>();
	private List entityList;

	@Override
	public void setEntityModels(Collection<EntityModel> entityModels) {
		
		// TODO Auto-generated method stub
		
	}
}
