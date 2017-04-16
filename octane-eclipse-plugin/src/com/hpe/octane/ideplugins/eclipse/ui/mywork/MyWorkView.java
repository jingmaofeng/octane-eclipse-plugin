package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.UserItemArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.DefaultRowEntityFields;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityMouseListener;
import com.hpe.octane.ideplugins.eclipse.ui.util.ErrorComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.NoWorkComposite;

public class MyWorkView extends OctaneViewPart {

    private static final ILog logger = Activator.getDefault().getLog();

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.MyWorkView";
    private static final String LOADING_MESSAGE = "Loading \"My Work\"";

    private MyWorkService myWorkService = Activator.getInstance(MyWorkService.class);
    private UserItemArrayEntityListData entityData = new UserItemArrayEntityListData();
    private EntityListComposite entityListComposite;

    private Action refreshAction = new Action() {
        private Job refreshJob = new Job(LOADING_MESSAGE) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                showLoading();
                monitor.beginTask(LOADING_MESSAGE, IProgressMonitor.UNKNOWN);
                Collection<EntityModel> entities;
                try {
                    entities = myWorkService.getMyWork(DefaultRowEntityFields.entityFields);
                    Display.getDefault().asyncExec(() -> {
                        entityData.setEntityList(entities);
                        if (entities.size() == 0) {
                            showControl(noWorkComposite);
                        } else {
                            showContent();
                        }
                    });
                } catch (Exception e) {
                    errorComposite.setErrorMessage("Error while loading \"My Work\"" + e.getMessage());
                    showControl(errorComposite);
                    entityData.setEntityList(Collections.emptyList());
                }
                monitor.done();
                return Status.OK_STATUS;
            }
        };

        @Override
        public void run() {
            refreshJob.schedule();
        }
    };

    /**
     * Shown when my work service returns an empty list
     */
    private NoWorkComposite noWorkComposite;
    private ErrorComposite errorComposite;

    @Override
    public Control createOctanePartControl(Composite parent) {
        entityListComposite = new EntityListComposite(parent, SWT.NONE, entityData, new MyWorkEntityModelMenuFactory(this));
        noWorkComposite = new NoWorkComposite(parent, SWT.NONE);
        errorComposite = new ErrorComposite(parent, SWT.NONE);

        // Add refresh action to view
        IActionBars viewToolbar = getViewSite().getActionBars();
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refresh \"My Work\"");
        refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh-16x16.png"));
        viewToolbar.getToolBarManager().add(refreshAction);

        // Init
        if (!Activator.getConnectionSettings().isEmpty()) {
            refreshAction.setEnabled(true);
            refreshAction.run();
        } else {
            refreshAction.setEnabled(false);
        }

        Activator.addConnectionSettingsChangeHandler(() -> {
            if (!Activator.getConnectionSettings().isEmpty()) {
                refreshAction.setEnabled(true);
                refreshAction.run();
            } else {
                refreshAction.setEnabled(false);
            }
        });

        // Mouse handlers
        entityListComposite.addEntityMouseListener(new EntityMouseListener() {
            @Override
            public void mouseClick(EntityModel entityModel, MouseEvent event) {
                // Open detail tab
                if (event.count == 2) {
                    IWorkbenchPage page = getViewSite().getWorkbenchWindow().getActivePage();

                    if (Entity.USER_ITEM == Entity.getEntityType(entityModel)) {
                        entityModel = MyWorkUtil.getEntityModelFromUserItem(entityModel);
                    }

                    if (Entity.COMMENT == Entity.getEntityType(entityModel)) {
                        entityModel = (EntityModel) Util.getContainerItemForCommentModel(entityModel).getValue();
                    }

                    Long id = Long.parseLong(entityModel.getValue("id").getValue().toString());
                    EntityModelEditorInput entityModelEditorInput = new EntityModelEditorInput(id, Entity.getEntityType(entityModel));
                    try {
                        logger.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK, entityModelEditorInput.toString(), null));
                        page.openEditor(entityModelEditorInput, EntityModelEditor.ID);
                    } catch (PartInitException ex) {
                        logger.log(
                                new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, "An exception has occured when opening the editor", ex));
                    }
                }
            }
        });

        // Return root
        return entityListComposite;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

}
