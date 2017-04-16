package com.hpe.octane.ideplugins.eclipse.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.filter.UserItemArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.rowrenderer.MyWorkEntityModelRowRenderer;
import com.hpe.octane.ideplugins.eclipse.ui.mywork.MyWorkEntityModelMenuFactory;

public class DebugSwtShell {

    protected Shell shell;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            DebugSwtShell window = new DebugSwtShell();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        try {
            shell.open();
            shell.layout();
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
        display.dispose();
        System.exit(0);
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(1280, 720);
        Monitor primary = shell.getDisplay().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
        shell.setText("SWT Application");
        shell.setLayout(new GridLayout(1, false));

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        EntityListData entityListData = new UserItemArrayEntityListData();
        EntityListComposite entityListComposite = new EntityListComposite(shell, SWT.NONE, entityListData,
                new MyWorkEntityModelMenuFactory(null, entityListData));
        entityListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, true, 1, 1));

        Collection<EntityModel> myWork = DebugUtil.getMyWork(MyWorkEntityModelRowRenderer.getRequiredFields());
        Collection<EntityModel> xMyWork = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            xMyWork.addAll(myWork);
        }

        entityListData.setEntityList(myWork);
    }
}
