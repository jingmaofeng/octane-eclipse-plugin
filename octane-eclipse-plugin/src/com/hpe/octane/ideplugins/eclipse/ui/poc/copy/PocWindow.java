package com.hpe.octane.ideplugins.eclipse.ui.poc.copy;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.BorderLayout;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.EntityHeaderComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.EntityFieldsComposite;
import com.hpe.octane.ideplugins.eclipse.ui.comment.EntityCommentComposite;

public class PocWindow {

    protected Shell shell;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            PocWindow window = new PocWindow();
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
        shell.setLayout(new BorderLayout());
        
        EntityHeaderComposite entityHeaderComposite = new EntityHeaderComposite(shell, SWT.NONE);
        entityHeaderComposite.setLayoutData(BorderLayout.NORTH);
        
        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(BorderLayout.CENTER);
        composite.setLayout(new BorderLayout());
        
        EntityFieldsComposite entityFieldsComposite = new EntityFieldsComposite(composite, SWT.NONE);
        entityFieldsComposite.setLayoutData(BorderLayout.CENTER);
        
        EntityCommentComposite entityCommentComposite = new EntityCommentComposite(composite, SWT.NONE);
        entityCommentComposite.setLayoutData(BorderLayout.EAST);
    }

}
