package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.GetCommentsJob;

public class EntityComposite extends Composite {

	@SuppressWarnings("unused")
	private EntityModel entityModel;

	private EntityCommentComposite entityCommentComposite;
	private EntityHeaderComposite entityHeaderComposite;
	private EntityFieldsComposite entityFieldsComposite;

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

		entityFieldsComposite = new EntityFieldsComposite(this, SWT.NONE);
		entityFieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

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

	public void createFields(EntityModel entityModel) {
		entityFieldsComposite.createFieldsSection(entityModel);
		entityFieldsComposite.createDescriptionFormSection(entityModel);
	}

	private void showOrHideComments(EntityModel entityModel) {
		if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
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
