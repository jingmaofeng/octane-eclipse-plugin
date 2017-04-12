package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class TestDetailsEntinty {
	protected Form form;
	protected FormToolkit toolkit;
	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestDetailsEntinty window = new TestDetailsEntinty();
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
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		 Composite composite = new Composite(shell, SWT.NULL);
		 composite.setLayout(new FillLayout());

			
		 // Sets up the toolkit.
	    toolkit = new FormToolkit(shell.getDisplay());

	    // Creates a form instance.
	    form = toolkit.createForm(composite);
//	    form.setLayoutData(new GridData(GridData.FILL_BOTH));

	    // Sets title.
	    form.setText("Custom Form Widgets Demo");
	    demoSections();
		
	}
	
	 private void demoSections() {
		    form.getBody().setLayout(new TableWrapLayout());
		    
		    Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION | 
		        Section.TREE_NODE | Section.EXPANDED);
		    
		    section.setText("This is the title");
		    toolkit.createCompositeSeparator(section);
		    section.setDescription("-= This is a description -=");
		    
		    FormText text = toolkit.createFormText(section, false);
		    text.setText(
		      "This is a long text. The user can show or hide this text "
		        + "by expanding or collapsing the expandable composite.",
		      false,
		      false);
		    section.setClient(text);
		  }

}