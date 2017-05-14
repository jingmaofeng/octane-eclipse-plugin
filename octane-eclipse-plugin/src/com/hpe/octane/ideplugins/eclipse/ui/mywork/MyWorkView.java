package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.UserItemArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.OctaneViewPart;
import com.hpe.octane.ideplugins.eclipse.ui.editor.snake.SnakeEditor;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.DefaultRowEntityFields;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.FatlineEntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.mywork.rowrenderer.MyWorkEntityModelRowRenderer;
import com.hpe.octane.ideplugins.eclipse.ui.search.SearchEditor;
import com.hpe.octane.ideplugins.eclipse.ui.search.SearchEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.util.ErrorComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.OpenDetailTabEntityMouseListener;
import com.hpe.octane.ideplugins.eclipse.ui.util.SeparatorControlContribution;
import com.hpe.octane.ideplugins.eclipse.ui.util.TextContributionItem;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class MyWorkView extends OctaneViewPart {

    private static final ILog logger = Activator.getDefault().getLog();

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.mywork.MyWorkView";
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
                    Display.getDefault().asyncExec(() -> {
                        errorComposite.setErrorMessage("Error while loading \"My Work\": " + e.getMessage());
                        showControl(errorComposite);
                        entityData.setEntityList(Collections.emptyList());
                    });
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
    private TextContributionItem textContributionItem;

    @Override
    public Control createOctanePartControl(Composite parent) {
        parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        entityListComposite = new EntityListComposite(
                parent,
                SWT.NONE,
                entityData,
                (viewerParent) -> {
                    FatlineEntityListViewer viewer = new FatlineEntityListViewer((Composite) viewerParent,
                            SWT.NONE,
                            new MyWorkEntityModelMenuFactory(entityData),
                            new MyWorkEntityModelRowRenderer());

                    Activator.addActiveItemChangedHandler(() -> {
                        viewer.recreateRows();
                    });

                    return viewer;
                });

        noWorkComposite = new NoWorkComposite(parent, SWT.NONE, new Runnable() {
            @Override
            public void run() {
                IWorkbenchPage currentPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    currentPage.openEditor(SnakeEditor.snakeEditorInput, SnakeEditor.ID);
                } catch (PartInitException ignored) {
                }
            }
        });
        errorComposite = new ErrorComposite(parent, SWT.NONE);

        IActionBars viewToolbar = getViewSite().getActionBars();

        // Add search action to view toolbar
        textContributionItem = new TextContributionItem(ID + ".searchtext");
        textContributionItem.setControlCreatedRunnable(
                () -> {
                    textContributionItem.setMessage("Global search");
                    textContributionItem.addTraverseListener(new TraverseListener() {
                        @Override
                        public void keyTraversed(TraverseEvent e) {
                            if (e.detail == SWT.TRAVERSE_RETURN) {
                                // Open search editor
                                SearchEditorInput searchEditorInput = new SearchEditorInput(textContributionItem.getText());
                                try {
                                    logger.log(new Status(
                                            Status.INFO,
                                            Activator.PLUGIN_ID,
                                            Status.OK,
                                            searchEditorInput.toString(),
                                            null));

                                    MyWorkView.this.getSite().getPage()
                                            .openEditor(searchEditorInput, SearchEditor.ID);

                                } catch (PartInitException ex) {
                                    logger.log(new Status(
                                            Status.ERROR,
                                            Activator.PLUGIN_ID,
                                            Status.ERROR,
                                            "An exception has occured when opening the editor",
                                            ex));
                                }
                            }
                        }
                    });
                });
        viewToolbar.getToolBarManager().add(textContributionItem);

        viewToolbar.getToolBarManager().add(new SeparatorControlContribution(ID + ".separator"));

        // Add refresh action to view toolbar
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refresh \"My Work\"");
        refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh-16x16.png"));
        ActionContributionItem refreshActionItem = new ActionContributionItem(refreshAction);
        refreshActionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
        viewToolbar.getToolBarManager().add(refreshActionItem);

        // Mouse handlers
        entityListComposite.addEntityMouseListener(new OpenDetailTabEntityMouseListener());

        // Init
        Runnable initRunnable = () -> {
            if (!Activator.getConnectionSettings().isEmpty()) {
                refreshAction.setEnabled(true);
                textContributionItem.setEnabled(true);
                refreshAction.run();
            } else {
                showWelcome();
                refreshAction.setEnabled(false);
                textContributionItem.setEnabled(false);
            }
        };

        Activator.addConnectionSettingsChangeHandler(initRunnable);
        initRunnable.run();

        // Return root
        return entityListComposite;
    }

    public void refresh() {
        refreshAction.run();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

}