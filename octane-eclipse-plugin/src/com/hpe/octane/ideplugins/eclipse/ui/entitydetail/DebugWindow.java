package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

import swing2swt.layout.BorderLayout;

public class DebugWindow {

    protected Shell shell;
    protected Display display;

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

}
