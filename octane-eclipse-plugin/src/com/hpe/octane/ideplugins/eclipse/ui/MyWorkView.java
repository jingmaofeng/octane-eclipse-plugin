package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.MyWorkService;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class MyWorkView extends ViewPart {

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.MyWorkView";

    private static final String LOADING_MESSAGE = "Loading \"My Work\"";

    private ListViewer viewer;

    private MyWorkService myWorkService = Activator.getInstance(MyWorkService.class);

    @Override
    public void createPartControl(Composite parent) {
        // Init view
        viewer = new ListViewer(parent);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                EntityModel p = (EntityModel) element;
                return p.getValue("id").getValue().toString() + ": " + p.getValue("name").getValue().toString();
            };
        });
        getSite().setSelectionProvider(viewer);
        hookDoubleClickCommand();

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
                        viewer.setInput(myWorkService.getMyWork());
                    } catch (Exception e) {
                        MessageDialog.openError(getSite().getShell(), "Error while loading \"My Work\"", e.toString());
                        viewer.setInput(null);
                    }
                });
                monitor.done();
                return Status.OK_STATUS;
            }
        };
    }

    private void hookDoubleClickCommand() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IHandlerService handlerService = getSite().getService(IHandlerService.class);
                try {

                    Object obj = viewer.getStructuredSelection().getFirstElement();
                    if (obj instanceof EntityModel) {
                        // ContributionItem1.setLblText(((EntityModel)
                        // obj).getValue("name").getValue().toString());
                    }

                    handlerService.executeCommand("octane-eclipse-plugin.openEntityEditor", null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        });
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
}
