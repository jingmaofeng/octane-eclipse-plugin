package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.GetCommentsJob;

public class EntityComposite extends Composite {

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
		setLayout(new GridLayout(2, false));

		entityHeaderComposite = new EntityHeaderComposite(this, SWT.NONE);
		entityHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		entityFieldsComposite = new EntityFieldsComposite(sashForm, SWT.PUSH);
		entityCommentComposite = new EntityCommentComposite(sashForm, SWT.PUSH);
		
		entityHeaderComposite.addCommentsSelectionListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
					entityCommentComposite.setVisible(!entityCommentComposite.getVisible());
				}
			}
		});
	}

	public void setEntityModel(EntityModel entityModel) {
		this.entityModel = entityModel;
		entityHeaderComposite.setEntityModel(entityModel);
		entityFieldsComposite.setEntityModel(entityModel);		
		showOrHideComments(entityModel);
	}

	private void showOrHideComments(EntityModel entityModel) {
		if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
			//GridData gridData = (GridData) entityCommentComposite.getLayoutData();
			//gridData.exclude = false;
			entityCommentComposite.setVisible(true);
			entityCommentComposite.setEntityModel(entityModel);
		} else {
//			GridData gridData = (GridData) entityCommentComposite.getLayoutData();
//			gridData.exclude = true;
			entityCommentComposite.setVisible(false);
		}
	}

	public void addSaveSelectionListener(Listener listener) {
		entityHeaderComposite.addSaveSelectionListener(listener);
	}

	public void addRefreshSelectionListener(Listener listener) {
		entityHeaderComposite.addRefreshSelectionListener(listener);
	}
	
	public EntityModel getSelectedPhase() {
		return entityHeaderComposite.getSelectedPhase();
	}
}
