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
package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.GetEntityModelJob;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.UpdateEntityJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.InfoPopup;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.icon.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

public class EntityModelEditor extends EditorPart {

    public EntityModelEditor() {
    }

    private static final String GO_TO_BROWSER_DIALOG_MESSAGE = "You can try to change the phase using ALM Octane in a browser."
            + "\nDo you want to do this now?";

    private static final EntityIconFactory entityIconFactoryForTabInfo = new EntityIconFactory(20, 20, 7);
    private static EntityService entityService = Activator.getInstance(EntityService.class);

    private static final Color BACKGROUND_COLOR = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme()
            .getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.editor2.EntityModelEditorNew"; //$NON-NLS-1$

    public EntityModelEditorInput input;
    private EntityModel entityModel;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {

        if (!(input instanceof EntityModelEditorInput)) {
            throw new RuntimeException("Wrong input");
        }
        this.input = (EntityModelEditorInput) input;

        setSite(site);
        setInput(input);

        setPartName(String.valueOf(this.input.getId()));
        setTitleImage(entityIconFactoryForTabInfo.getImageIcon(this.input.getEntityType()));
    }

    @Override
    public void createPartControl(Composite parent) {

        StackLayoutComposite rootComposite = new StackLayoutComposite(parent, SWT.NONE);
        rootComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        rootComposite.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
        rootComposite.setBackground(BACKGROUND_COLOR);

        LoadingComposite loadingComposite = new LoadingComposite(rootComposite, SWT.NONE);

        ScrolledComposite entityScrolledComposite = new ScrolledComposite(rootComposite, SWT.HORIZONTAL | SWT.VERTICAL);
        EntityComposite entityComposite = new EntityComposite(entityScrolledComposite, SWT.NONE);
        entityScrolledComposite.setContent(entityComposite);
        entityScrolledComposite.setExpandHorizontal(true);
        entityScrolledComposite.setExpandVertical(true);
        entityScrolledComposite.setMinSize(new Point(800, 1000));

        rootComposite.showControl(loadingComposite);

        GetEntityModelJob getEntityDetailsJob = new GetEntityModelJob("Retrieving entity details",
                input.getEntityType(), input.getId());

        getEntityDetailsJob.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    rootComposite.showControl(loadingComposite);
                });
            }

            @Override
            public void done(IJobChangeEvent event) {
                if (getEntityDetailsJob.wasEntityRetrived()) {
                    EntityModelEditor.this.entityModel = getEntityDetailsJob.getEntiyData();
                    Display.getDefault().asyncExec(() -> {
                        entityComposite.setEntityModel(entityModel);
                        rootComposite.showControl(entityScrolledComposite);
                    });
                } else {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
                            "Failed to load entity, " + getEntityDetailsJob.getException());
                }
            }
        });

        entityComposite.addRefreshSelectionListener(event -> getEntityDetailsJob.schedule());

        entityComposite.addSaveSelectionListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
                UpdateEntityJob updateEntityJob = new UpdateEntityJob("Saving " + Entity.getEntityType(entityModel), entityModel);
                updateEntityJob.schedule();
                updateEntityJob.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        Display.getDefault().asyncExec(() -> {
                            if (updateEntityJob.isPhaseChanged()) {
                                new InfoPopup("Saving entity", "Saved your changes").open();
                            } else {
                                boolean shouldGoToBrowser = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                                        "Business rule violation",
                                        "Phase change failed \n" + GO_TO_BROWSER_DIALOG_MESSAGE);
                                if (shouldGoToBrowser) {
                                    entityService.openInBrowser(entityModel);
                                }
                            }
                            getEntityDetailsJob.schedule();
                        });
                    }
                });
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