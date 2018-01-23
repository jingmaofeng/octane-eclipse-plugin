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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.StackLayout;

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
		fieldsComposite.setLayout(new GridLayout(2, false));
		fieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblNewLabel = new Label(fieldsComposite, SWT.CENTER);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("New Label");
		
		Label lblNewLabel_1 = new Label(fieldsComposite, SWT.CENTER);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_1.setText("New Label");
		
		browser = new Browser(fieldsComposite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		browser.setUrl("http://google.com");
		
		entityCommentComposite = new EntityCommentComposite(this, SWT.NONE);
		GridData gd_entityCommentComposite = new GridData(300, SWT.CENTER);
		gd_entityCommentComposite.verticalAlignment = SWT.FILL;
		gd_entityCommentComposite.grabExcessVerticalSpace = true;
		gd_entityCommentComposite.grabExcessHorizontalSpace = false;
		entityCommentComposite.setLayoutData(gd_entityCommentComposite);
		
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
