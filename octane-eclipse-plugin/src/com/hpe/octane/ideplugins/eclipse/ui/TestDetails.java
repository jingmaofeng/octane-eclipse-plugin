package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import com.hpe.octane.ideplugins.eclipse.Activator;

public class TestDetails extends ViewPart {
	public TestDetails() {
	}

    public static final String  ID              = "com.hpe.octane.ideplugins.eclipse.ui.TestDetails";

    private static final String LOADING_MESSAGE = "Loading \"Details\"";

    protected Form form;
	protected FormToolkit toolkit;
	protected Shell shell;
    

    @Override
    public void createPartControl(Composite parent) {
    	shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		 Composite composite = new Composite(parent, SWT.NULL);
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

   

    @Override
    public void setFocus() {
    	form.setFocus();
    }
}
