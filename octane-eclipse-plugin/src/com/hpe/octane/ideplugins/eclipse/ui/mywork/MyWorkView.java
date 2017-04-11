package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.UserItemArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.DefaultRowEntityFields;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;

public class MyWorkView extends ViewPart {

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.MyWorkView";
    private static final String LOADING_MESSAGE = "Loading \"My Work\"";

    private MyWorkService myWorkService = Activator.getInstance(MyWorkService.class);
    private UserItemArrayEntityListData entityData = new UserItemArrayEntityListData();
    private EntityListComposite entityListComposite;

    @Override
    public void createPartControl(Composite parent) {
        entityListComposite = new EntityListComposite(parent, SWT.NONE, entityData);

        // Initial fill
        Job refreshJob = createRefreshJob();
        refreshJob.schedule();

        // Add refresh
        IActionBars viewToolbar = getViewSite().getActionBars();
        Action refreshAction = new Action() {
            @Override
            public void run() {
                refreshJob.schedule();
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refresh \"My Work\"");
        refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh-16x16.png"));
        viewToolbar.getToolBarManager().add(refreshAction);

        Activator.addConnectionSettingsChangeHandler(() -> {
            refreshJob.schedule();
        });
    }

    private Job createRefreshJob() {
        return new Job(LOADING_MESSAGE) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(LOADING_MESSAGE, IProgressMonitor.UNKNOWN);
                Display.getDefault().asyncExec(() -> {
                    try {
                        entityData.setEntityList(myWorkService.getMyWork(DefaultRowEntityFields.entityFields));
                    } catch (Exception e) {
                        MessageDialog.openError(getSite().getShell(), "Error while loading \"My Work\"", e.toString());
                        entityData.setEntityList(Collections.emptyList());
                    }
                });
                monitor.done();
                return Status.OK_STATUS;
            }
        };
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }
}
