/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
		setLayout(new GridLayout(1, false));

		entityHeaderComposite = new EntityHeaderComposite(this, SWT.NONE);
		entityHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		entityFieldsComposite = new EntityFieldsComposite(sashForm, SWT.PUSH);
		entityCommentComposite = new EntityCommentComposite(sashForm, SWT.PUSH);
		
		entityHeaderComposite.addCommentsSelectionListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
					entityCommentComposite.setVisible(!entityCommentComposite.getVisible());
					
					// Force redraw
					layout(false, true);
					redraw();
					update();
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
			entityCommentComposite.setVisible(true);
			entityCommentComposite.setEntityModel(entityModel);
		} else {
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
