package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hpe.octane.ideplugins.eclipse.ui.EntityModelRow.DetailsPosition;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

public class FramyFrameFrame {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FramyFrameFrame window = new FramyFrameFrame();
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
		
		EntityModelRow entityModelRow = new EntityModelRow(shell, SWT.ALL);
		entityModelRow.setEntityName("Implement the whole eclipse plugin");
		entityModelRow.setEntityId(666);
		entityModelRow.setEntityDetails("Release: 2 releases ago");
		
		entityModelRow.addDetails("WHY", "ME", DetailsPosition.TOP);
		entityModelRow.addDetails("SP", "69", DetailsPosition.TOP);
		entityModelRow.addDetails("Severity", "Critical", DetailsPosition.TOP);
		
		entityModelRow.addDetails("Please", "stop", DetailsPosition.BOTTOM);
		entityModelRow.addDetails("Author", "Satan", DetailsPosition.BOTTOM);
		entityModelRow.addDetails("Owner", "you", DetailsPosition.BOTTOM);
	}

}
