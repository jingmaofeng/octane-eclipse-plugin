package com.hpe.octane.ideplugins.eclipse.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.filter.ArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.NoWorkComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;

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
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(450, 300);
        shell.setText("SWT Application");
        shell.setLayout(new FillLayout(SWT.VERTICAL));

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        Button btnSwtich = new Button(composite, SWT.NONE);
        btnSwtich.setText("Swtich");

        StackLayoutComposite stackLayoutComposite = new StackLayoutComposite(shell, SWT.BORDER);
        // LoadingComposite loadingComposite = new
        // LoadingComposite(stackLayoutComposite, SWT.NONE);
        NoWorkComposite noWorkComposite = new NoWorkComposite(stackLayoutComposite, SWT.NONE);
        // WelcomeComposite welcomeComposite = new
        // WelcomeComposite(stackLayoutComposite, SWT.NONE);

        EntityListData entityListData = new ArrayEntityListData();
        EntityListComposite entityListComposite = new EntityListComposite(stackLayoutComposite, SWT.NONE, entityListData);
        entityListData.setEntityList(DebugUtil.getEntitites(Entity.WORK_ITEM));

        // stackLayoutComposite.showControl(welcomeComposite);

        btnSwtich.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (Control control : stackLayoutComposite.getChildren()) {
                    if (control != stackLayoutComposite.getCurrentControl()) {
                        stackLayoutComposite.showControl(control);
                        return;
                    }
                }
            }
        });

    }
}
