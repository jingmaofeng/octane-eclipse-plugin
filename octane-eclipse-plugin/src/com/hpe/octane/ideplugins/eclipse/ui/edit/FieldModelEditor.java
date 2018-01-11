package com.hpe.octane.ideplugins.eclipse.ui.edit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.metadata.FieldMetadata.FieldType;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.LongFieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class FieldModelEditor extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FieldModelEditor(Composite parent, int style, EntityModel entityModel,  FieldModel fieldModel, FieldMetadata fieldMetadata) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		
		
		setLayout(new GridLayout(4, false));
		
		Label lblFieldName = new Label(this, SWT.NONE);		
		lblFieldName.setText(fieldMetadata.getLabel());
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		//fieldModel.getName().equals("story_points")
	
		if(fieldModel instanceof LongFieldModel && (fieldMetadata.getFieldType().equals(FieldType.Integer) || fieldMetadata.getFieldType().equals(FieldType.Float))) {
			
			Text textField = new Text(this, SWT.BORDER);
			textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textField.setText(Util.getUiDataFromModel(fieldModel));
			toolkit.adapt(textField, true, true);
			textField.addModifyListener(e -> {
				
				((LongFieldModel)fieldModel).setValue(fieldModel.getName(), Long.parseLong(textField.getText()));
				entityModel.setValue(fieldModel);
				
				System.out.println("Field model: " + fieldModel.getName() + " " + Util.getUiDataFromModel(fieldModel));
			});
			
			
 		} else {
 			Label lblFieldValue = new Label(this, SWT.NONE);
			lblFieldValue.setText(Util.getUiDataFromModel(fieldModel));
 			lblFieldValue.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
 			lblFieldName.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
 		}
		
	}

}
