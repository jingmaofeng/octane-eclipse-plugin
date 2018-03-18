package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.connection.ConnectionSettings;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.GetEntityModelJob;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.UpdateEntityJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.InfoPopup;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

import swing2swt.layout.BorderLayout;

public class DebugWindow {

    protected Shell shell;
    protected Display display;
    private EntityCommentComposite entityCommentComposite;
    private EntityFieldsComposite entityFieldsComposite;
    private EntityHeaderComposite entityHeaderComposite;
    private EntityModel entityModel;

    public static void main(String[] args) {
        initMockActivator();
        try {
            DebugWindow window = new DebugWindow();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        display = Display.getDefault();
        try {
            shell = new Shell(display);
            try {
                createContents();
                shell.open();
                while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
            } finally {
                if (!shell.isDisposed()) {
                    shell.dispose();
                }
            }
        } finally {
            display.dispose();
        }
        System.exit(0);
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(1200, 800);
        shell.setText("Edit me!");
        shell.setLayout(new BorderLayout());
        shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        shell.setBackgroundMode(SWT.INHERIT_FORCE);
        positionShell();

        entityHeaderComposite = new EntityHeaderComposite(shell, SWT.NONE);
        entityHeaderComposite.setLayoutData(BorderLayout.NORTH);

        ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.HORIZONTAL | SWT.VERTICAL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(new Point(800, 600));

        Composite fieldCommentParentComposite = new Composite(scrolledComposite, SWT.NONE);
        fieldCommentParentComposite.setLayoutData(BorderLayout.CENTER);
        fieldCommentParentComposite.setLayout(new BorderLayout());

        scrolledComposite.setContent(fieldCommentParentComposite);

        entityFieldsComposite = new EntityFieldsComposite(fieldCommentParentComposite, SWT.NONE);
        entityFieldsComposite.setLayoutData(BorderLayout.CENTER);

        entityCommentComposite = new EntityCommentComposite(fieldCommentParentComposite, SWT.NONE);
        entityCommentComposite.setLayoutData(BorderLayout.EAST);

        loadEntity();

        entityHeaderComposite.addRefreshSelectionListener(event -> {
            loadEntity();
        });
        entityHeaderComposite.addSaveSelectionListener(event -> {
            saveEntity();
        });
    }

    protected void loadEntity() {
        GetEntityModelJob getEntityDetailsJob = new GetEntityModelJob("Retrieving entity details", Entity.DEFECT, 146090L);

        getEntityDetailsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    // rootComposite.showControl(loadingComposite);
                });
            }

            @Override
            public void done(IJobChangeEvent event) {
                if (getEntityDetailsJob.wasEntityRetrived()) {
                    entityModel = getEntityDetailsJob.getEntiyData();
                    Display.getDefault().asyncExec(() -> {

                        entityCommentComposite.setEntityModel(entityModel);
                        entityFieldsComposite.setEntityModel(entityModel);
                        entityHeaderComposite.setEntityModel(entityModel);

                    });
                } else {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
                            "Failed to load entity, " + getEntityDetailsJob.getException());
                }
            }
        });

        getEntityDetailsJob.schedule();
    }

    private void saveEntity() {
        EntityService entityService = Activator.getInstance(EntityService.class);

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
                                "Phase change failed \n" + "Sorry!");
                        if (shouldGoToBrowser) {
                            entityService.openInBrowser(entityModel);
                        }
                    }
                    DebugWindow.this.loadEntity();
                });
            }
        });
    }

    private void positionShell() {
        // Monitor monitor = display.getPrimaryMonitor();
        Monitor monitor = display.getMonitors()[1];
        Rectangle bounds = monitor.getBounds();
        Rectangle rect = shell.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    private static void initMockActivator() {
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setBaseUrl("https://mqast001pngx.saas.hpe.com");
        connectionSettings.setSharedSpaceId(2004L);
        connectionSettings.setWorkspaceId(26002L);
        connectionSettings.setUserName(System.getProperty("username"));
        connectionSettings.setPassword(System.getProperty("password"));
        Activator.setConnectionSettings(connectionSettings);
    }

}
