package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

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
import com.hpe.adm.octane.ideplugins.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.ideplugins.services.connection.ConnectionSettings;
import com.hpe.adm.octane.ideplugins.services.di.ServiceModule;
import com.hpe.adm.octane.ideplugins.services.exception.ServiceException;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

import swing2swt.layout.BorderLayout;

public class DebugWindow {

    protected Shell shell;
    protected Display display;
    protected ServiceModule serviceModule = getServiceDIModule();

    public static void main(String[] args) {
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

        EntityHeaderComposite entityHeaderComposite = new EntityHeaderComposite(shell, SWT.NONE);
        entityHeaderComposite.setLayoutData(BorderLayout.NORTH);

        ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.HORIZONTAL | SWT.VERTICAL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(new Point(800, 600));

        Composite fieldCommentParentComposite = new Composite(scrolledComposite, SWT.NONE);
        fieldCommentParentComposite.setLayoutData(BorderLayout.CENTER);
        fieldCommentParentComposite.setLayout(new BorderLayout());

        scrolledComposite.setContent(fieldCommentParentComposite);

        EntityFieldsComposite entityFieldsComposite = new EntityFieldsComposite(fieldCommentParentComposite, SWT.NONE);
        entityFieldsComposite.setLayoutData(BorderLayout.CENTER);

        EntityCommentComposite entityCommentComposite = new EntityCommentComposite(fieldCommentParentComposite, SWT.NONE);
        entityCommentComposite.setLayoutData(BorderLayout.EAST);

        EntityModel entityModel = getEntityModel();
        entityCommentComposite.setEntityModel(entityModel);
        entityFieldsComposite.setEntityModel(entityModel);
        entityHeaderComposite.setEntityModel(entityModel);
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

    private ServiceModule getServiceDIModule() {
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setBaseUrl("https://mqast001pngx.saas.hpe.com");
        connectionSettings.setSharedSpaceId(2004L);
        connectionSettings.setWorkspaceId(26002L);
        connectionSettings.setUserName(System.getProperty("username"));
        connectionSettings.setPassword(System.getProperty("password"));
        Activator.setConnectionSettings(connectionSettings);
        ServiceModule module = new ServiceModule(new BasicConnectionSettingProvider(connectionSettings));
        return module;
    }

    private EntityModel getEntityModel() {
        try {
            return serviceModule.getInstance(EntityService.class).findEntity(Entity.DEFECT, 146090L);
        } catch (ServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

}
