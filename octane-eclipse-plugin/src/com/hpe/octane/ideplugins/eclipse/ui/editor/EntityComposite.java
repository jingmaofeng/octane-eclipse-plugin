package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.swt.SWT;
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
		
		entityFieldsComposite = new EntityFieldsComposite(this, SWT.NONE);
		entityFieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		entityCommentComposite = new EntityCommentComposite(this, SWT.NONE);
		GridData entityCommentCompositeGridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		entityCommentCompositeGridData.widthHint = 350;
		entityCommentComposite.setLayoutData(entityCommentCompositeGridData);
		
		entityHeaderComposite.addCommentsSelectionListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				setCommentsVisible(!entityCommentComposite.getVisible());
			}
		});
	}
	
	public void setCommentsVisible(boolean isVisible) {
		entityCommentComposite.setVisible(isVisible);
		((GridData) entityCommentComposite.getLayoutData()).exclude = !isVisible;
		layout(true, true);
		redraw();
		update();
	}

	public void setEntityModel(EntityModel entityModel) {
		this.entityModel = entityModel;
		
		entityHeaderComposite.setEntityModel(entityModel);
		entityFieldsComposite.setEntityModel(entityModel);	
		
		if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
			setCommentsVisible(true);
			entityCommentComposite.setEntityModel(entityModel);
		} else {
			setCommentsVisible(false);
		}
		
		setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
		layout(true, true);
		redraw();
		update();
	}
	
	public EntityModel getEntityModel() {
        return entityModel;
    }

    public void addSaveSelectionListener(Listener listener) {
		entityHeaderComposite.addSaveSelectionListener(listener);
	}

	public void addRefreshSelectionListener(Listener listener) {
		entityHeaderComposite.addRefreshSelectionListener(listener);
	}
	
}
