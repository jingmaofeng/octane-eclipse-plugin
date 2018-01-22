package com.hpe.octane.ideplugins.eclipse.ui.edit;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.metadata.FieldMetadata.FieldType;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.LongFieldModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

public class FieldModelEditor extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FieldModelEditor(Composite parent, int style, EntityModel entityModel, FieldModel fieldModel,
			FieldMetadata fieldMetadata) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout());

		if (fieldMetadata.getFieldType().equals(FieldType.Integer)
				|| fieldMetadata.getFieldType().equals(FieldType.Float)
				|| fieldModel.getName().equals("story_points")) {
			if (fieldMetadata.isEditable()) {
				Text textField = new Text(this, SWT.BORDER);
				textField.setText(Util.getUiDataFromModel(fieldModel));
				toolkit.adapt(textField, true, true);
				textField.addModifyListener(e -> {
					LongFieldModel newFieldModel = new LongFieldModel(fieldModel.getName(),
							Long.parseLong(textField.getText()));
					entityModel.setValue(newFieldModel);

					System.out.println(
							"Field model: " + fieldModel.getName() + " " + Util.getUiDataFromModel(fieldModel));
				});
			}
		}

		else if (fieldMetadata.getFieldType().equals(FieldType.String)) {
			if (fieldMetadata.isEditable()) {
				Text textField = new Text(this, SWT.BORDER);
				textField.setText(Util.getUiDataFromModel(fieldModel));
				toolkit.adapt(textField, true, true);
				textField.addModifyListener(e -> {

					StringFieldModel newFieldModel = new StringFieldModel(fieldModel.getName(), textField.getText());
					entityModel.setValue(newFieldModel);

					System.out.println(
							"Field model: " + fieldModel.getName() + " " + Util.getUiDataFromModel(fieldModel));
				});
			}
		}

		else {
			Label lblFieldValue = new Label(this, SWT.NONE);
			lblFieldValue.setText(Util.getUiDataFromModel(fieldModel));
			lblFieldValue.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
		}

	}

}
