package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.GetCommentsJob;

public class EntityComposite extends Composite {
	
	private EntityModel entityModel;
	private EntityCommentComposite entityCommentComposite;
	private EntityHeaderComposite entityHeaderComposite;
	private Composite fieldsComposite;
	private Browser browser;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EntityComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, true));
		
		entityHeaderComposite = new EntityHeaderComposite(this, SWT.NONE);
		entityHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		fieldsComposite = new Composite(this, SWT.NONE);
		fieldsComposite.setLayout(new GridLayout(2, true));
		fieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		Label lblNewLabel = new Label(fieldsComposite, SWT.NONE);
		lblNewLabel.setText("New Label");
		
		Label lblNewLabel_1 = new Label(fieldsComposite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_1.setText("New Label");
		
		browser = new Browser(fieldsComposite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		browser.setUrl("http://google.com");
		
		entityCommentComposite = new EntityCommentComposite(this, SWT.NONE);
		entityCommentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
	}
	
	public void setEntityModel(EntityModel entityModel) {
		this.entityModel = entityModel;

		entityHeaderComposite.setEntityModel(entityModel);
		showOrHideComments(entityModel);
	}
	
	private void showOrHideComments(EntityModel entityModel) {
		if(GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
			GridData gridData = (GridData) entityCommentComposite.getLayoutData();
			gridData.exclude = false;
			entityCommentComposite.setVisible(true);
			entityCommentComposite.setEntityModel(entityModel);
		} else {
			GridData gridData = (GridData) entityCommentComposite.getLayoutData();
			gridData.exclude = true;
			entityCommentComposite.setVisible(false);
		}
	}

	public void addSaveSelectionListener(Listener listener) {
		entityHeaderComposite.addSaveSelectionListener(listener);
	}

	public void addRefreshSelectionListener(Listener listener) {
		entityHeaderComposite.addRefreshSelectionListener(listener);
	}
}
