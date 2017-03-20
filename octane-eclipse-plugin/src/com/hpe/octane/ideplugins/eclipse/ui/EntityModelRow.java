package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class EntityModelRow extends Composite {
	
    public enum DetailsPosition {
    	TOP, BOTTOM
	}

	private Label lblEntityDetails;
	private Label lblEntityName;
	private Label lblEntityId;
	private Label lblEntityIcon;
	private Composite compositeTopDetails;
	private Composite compositeBottomDetails;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EntityModelRow(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));
		
		lblEntityIcon = new Label(this, SWT.NONE);
		lblEntityIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		
		lblEntityId = new Label(this, SWT.NONE);
		lblEntityId.setText("6666");
		
		lblEntityName = new Label(this, SWT.WRAP);
		lblEntityName.setText("Implement all thingz");
		
		compositeTopDetails = new Composite(this, SWT.BORDER);
		compositeTopDetails.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeTopDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		lblEntityDetails = new Label(this, SWT.WRAP);
		lblEntityDetails.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblEntityDetails.setText("Entity Details Here");
		
		compositeBottomDetails = new Composite(this, SWT.NONE);
		compositeBottomDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		compositeBottomDetails.setLayout(new FillLayout(SWT.HORIZONTAL));
	}
	
	public void setEntityIcon(Image entityIconImage){
		lblEntityIcon.setImage(entityIconImage);
	}
	
	public void setEntityName(String entityName){
		lblEntityName.setText(entityName);
	}
	
	public void setEntityId(Integer id){
		lblEntityId.setText(id+"");
	}
	
	public void setEntityDetails(String details){
		lblEntityDetails.setText(details);
	}
	
	public void addDetails(String fieldName, String fieldValue, DetailsPosition position){
		
		Composite parent;
		if(DetailsPosition.TOP == position){
			parent = compositeTopDetails;
		} else {
			parent = compositeBottomDetails;
		}
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblSeparator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.heightHint = 20;
		lblSeparator.setLayoutData(gd_label);
		
		Label lblKey = new Label(composite, SWT.NONE);
		lblKey.setText(fieldName);
		
		Label lblValue = new Label(composite, SWT.NONE);
		lblValue.setText(fieldValue);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
