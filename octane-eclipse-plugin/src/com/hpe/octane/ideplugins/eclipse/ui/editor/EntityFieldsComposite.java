package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.LinkInterceptListener;
import com.hpe.octane.ideplugins.eclipse.ui.util.PropagateScrollBrowserFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class EntityFieldsComposite extends Composite {

	private Color backgroundColor = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry()
			.get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
	private Color foregroundColor = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry()
			.get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);

	private Composite entityFieldsComposite;
	private Composite entityDescriptionComposite;

	private FormToolkit formGenerator;

	private Form sectionsParentForm;

	public EntityFieldsComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		formGenerator = new FormToolkit(parent.getDisplay());

		entityFieldsComposite = new Composite(this, SWT.NONE);
		entityFieldsComposite.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		entityFieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		entityFieldsComposite.setLayout(new GridLayout(2, false));
		formGenerator.adapt(entityFieldsComposite);
		formGenerator.paintBordersFor(entityFieldsComposite);

		entityDescriptionComposite = new Composite(this, SWT.NONE);
		entityDescriptionComposite.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		entityDescriptionComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true, 1, 1));
		entityDescriptionComposite.setLayout(new GridLayout(1, false));
		formGenerator.adapt(entityDescriptionComposite);
		formGenerator.paintBordersFor(entityDescriptionComposite);

	}

	public void createFieldsSection(EntityModel entityModel) {
		Section section = formGenerator.createSection(entityFieldsComposite, Section.TREE_NODE | Section.EXPANDED);
		section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		section.setText("Fields");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formGenerator.createCompositeSeparator(section);
//		 drawEntityFields(entityFieldsComposite);
//		section.setClient(entityFieldsComposite);
	}

	public Section createDescriptionFormSection(EntityModel entityModel) {
		Section section = formGenerator.createSection(entityDescriptionComposite, Section.TREE_NODE | Section.EXPANDED);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.minimumHeight = 400;
		section.setLayoutData(gd);
		formGenerator.createCompositeSeparator(section);
		section.setText("Description");

		PropagateScrollBrowserFactory factory = new PropagateScrollBrowserFactory();
		Browser descBrowser = factory.createBrowser(section, SWT.NONE);
		descBrowser.setLayoutData(gd);

		String descriptionText = "<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
				+ getRgbString(foregroundColor) + ">"
				+ Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION))
				+ "</font></body></html>";
		if (descriptionText.equals("<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
				+ getRgbString(foregroundColor) + ">" + "</font></body></html>")) {
			descBrowser.setText("<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
					+ getRgbString(foregroundColor) + ">" + "No description" + "</font></body></html>");
		} else {
			descBrowser.setText(descriptionText);
		}

		descBrowser.addLocationListener(new LinkInterceptListener());
		section.setClient(descBrowser);
		return section;
	}

	// private void drawEntityFields(Composite parent, Set<String> shownFields) {
	// Arrays.stream(parent.getChildren())
	// .filter(child -> child != null)
	// .filter(child -> !child.isDisposed())
	// .forEach(child -> child.dispose());
	//
	// parent.setLayout(new FillLayout(SWT.HORIZONTAL));
	// Composite sectionClientLeft = new Composite(parent, SWT.NONE);
	// sectionClientLeft.setLayout(new GridLayout(2, false));
	// sectionClientLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
	// Composite sectionClientRight = new Composite(parent, SWT.NONE);
	// sectionClientRight.setLayout(new GridLayout(2, false));
	// sectionClientRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
	//
	// // Skip the description fields as it's set into another ui component
	// // below this one
	// shownFields.remove(EntityFieldsConstants.FIELD_DESCRIPTION);
	//
	// Iterator<String> iterator = shownFields.iterator();
	//
	// for (int i = 0; i < shownFields.size(); i++) {
	// String fieldName = iterator.next();
	// String fielValue;
	// if (EntityFieldsConstants.FIELD_OWNER.equals(fieldName)
	// || EntityFieldsConstants.FIELD_AUTHOR.equals(fieldName)
	// || EntityFieldsConstants.FIELD_TEST_RUN_RUN_BY.equals(fieldName)
	// || EntityFieldsConstants.FIELD_DETECTEDBY.equals(fieldName)) {
	// fielValue = Util.getUiDataFromModel(entityModel.getValue(fieldName),
	// EntityFieldsConstants.FIELD_FULL_NAME);
	// } else {
	// fielValue = Util.getUiDataFromModel(entityModel.getValue(fieldName));
	// }
	//
	// // Determine if we put the label pair in the left or right container
	// Composite columnComposite;
	// if (i % 2 == 0) {
	// columnComposite = sectionClientLeft;
	// } else {
	// columnComposite = sectionClientRight;
	// }
	//
	// // Add the pair of labels for field and value
	// CLabel labelFieldName = new CLabel(columnComposite, SWT.NONE);
	// labelFieldName.setText(prettyFieldsMap.get(fieldName));
	//
	// labelFieldName.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
	//
	// labelFieldName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
	// false, false, 1, 1));
	//
	// TruncatingStyledText labelValue = new TruncatingStyledText(columnComposite,
	// SWT.NONE, truncatedLabelTooltip);
	// labelValue.setText(fielValue);
	// labelValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
	// 1));
	// // labelValue.setForeground(foregroundColor);
	// }
	// }
	//
	// private void drawEntityFields(Composite parent) {
	// Set<String> shownFields =
	// PluginPreferenceStorage.getShownEntityFields(input.getEntityType());
	// drawEntityFields(parent, shownFields);
	// }

	private static String getRgbString(Color color) {
		return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}

}
