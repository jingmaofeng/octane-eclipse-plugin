package com.hpe.octane.ideplugins.eclipse.dev;

import java.util.stream.IntStream;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hpe.octane.ideplugins.eclipse.ui.util.MultiSelectComboBox;

public class TestWindow {

    protected Shell shell;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            TestWindow window = new TestWindow();
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
        shell.setLayout(new GridLayout(1, false));

        MultiSelectComboBox<String> fieldCombo = new MultiSelectComboBox<String>(shell, SWT.NONE, new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });

        fieldCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println(fieldCombo.getSelections());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });

        IntStream.range(0, 100).forEach(i -> fieldCombo.add(i + ""));
    }

}
