package com.hpe.octane.ideplugins.eclipse.ui.editor.devtools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

public class TestSectionView2 {

    protected Shell shell;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            TestSectionView2 window = new TestSectionView2();
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
        shell.setSize(794, 540);
        shell.setText("SWT Application");
        shell.setLayout(new GridLayout(1, false));

        Form frmNewForm = new Form(shell, SWT.BORDER);
        frmNewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        frmNewForm.setText("New Form");
        frmNewForm.getBody().setLayout(new GridLayout(1, false));

        Section sctnNewSection = new Section(frmNewForm.getBody(), Section.TWISTIE | Section.TITLE_BAR);
        sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sctnNewSection.setText("New Section");

    }
}
