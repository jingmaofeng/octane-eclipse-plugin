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
package com.hpe.octane.ideplugins.eclipse.ui.editor2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import org.eclipse.swt.layout.GridData;

public class EntityModelEditorNew extends EditorPart {
	
	public EntityModelEditorNew() {
	}

	public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.editor2.EntityModelEditorNew"; //$NON-NLS-1$
	public EntityModelEditorInput input;
	
	private Composite entityComposite;
	private Label errorLabel;
	private LoadingComposite loadingComposite;
	private StackLayoutComposite rootComposite;
	private EntityHeaderComposite entityHeaderComposite;
	private Composite composite;
	private Composite composite_1;
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	    
		if (!(input instanceof EntityModelEditorInput)) {
            throw new RuntimeException("Wrong input");
        }
	    this.input = (EntityModelEditorInput) input;
		
	    setSite(site);
		setInput(input);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		rootComposite = new StackLayoutComposite(parent, SWT.NONE);
		
		loadingComposite = new LoadingComposite(rootComposite, SWT.NONE);
		
     	errorLabel = new Label(rootComposite, SWT.NONE);
    	errorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		entityComposite = new Composite(rootComposite, SWT.NONE);
		entityComposite.setLayout(new GridLayout(2, false));		
		
		entityHeaderComposite = new EntityHeaderComposite(entityComposite, SWT.NONE);
		GridData gd_entityHeaderComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_entityHeaderComposite.widthHint = 301;
		entityHeaderComposite.setLayoutData(gd_entityHeaderComposite);
		
		composite = new Composite(entityComposite, SWT.NONE);
		
		composite_1 = new Composite(entityComposite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
        // This job retrieves the necessary data for the details view
		GetEntityDetailsJobNew getEntityDetailsJob = new GetEntityDetailsJobNew("Retrieving entity details", input.getEntityType(), input.getId());
        getEntityDetailsJob.addJobChangeListener(new JobChangeAdapter() {
        	
            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> rootComposite.showControl(loadingComposite));
            }
                       
            @Override
            public void done(IJobChangeEvent event) {
            	  	
                if (getEntityDetailsJob.wasEntityRetrived() == null) {
                	
                	Display.getDefault().asyncExec(() -> rootComposite.showControl(entityComposite));
                	
//                    entityModel = getEntityDetailsJob.getEntiyData();
//                    
//                    
//                    Display.getDefault().asyncExec(() -> {
//                        entityModel = getEntityDetailsJob.getEntiyData();
//
//                        if (getEntityDetailsJob.shouldShowPhase()) {
//                            shouldShowPhase = true;
//                            currentPhase = getEntityDetailsJob.getCurrentPhase();
//                            possibleTransitions = getEntityDetailsJob.getPossibleTransitionsForCurrentEntity();
//                        } else {
//                            shouldShowPhase = false;
//                        }
//
//                        // After the data is loaded the UI is created
//                        createEntityDetailsView(rootComposite);
//
//                        // After the UI is created it gets displayed
//                        rootComposite.showControl(headerAndEntityDetailsScrollComposite);
//                    });
                        
                        
                } else {
                	Display.getDefault().asyncExec(() -> {
                    	errorLabel.setText(getEntityDetailsJob.wasEntityRetrived().getMessage());
                		rootComposite.showControl(errorLabel); 
            		});
                }
            } 
            
        });
        
        getEntityDetailsJob.schedule();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Not supported
	}

	@Override
	public void doSaveAs() {
		// Not supported
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}