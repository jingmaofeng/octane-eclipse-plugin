package com.hpe.octane.ideplugins.eclipse.ui;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.ConnectionSettingsProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormField;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;

public class TestDetailsEntinty {
	protected Form form;
	protected FormToolkit toolkit;
	protected Shell shell;

	/**
	 * Launch the application.
	 * 
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
		shell.setSize(487, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(shell, SWT.NULL);
		composite.setLayout(new FillLayout());

		// Sets up the toolkit.
		toolkit = new FormToolkit(shell.getDisplay());

		// Creates a form instance.
		form = toolkit.createForm(composite);
		// form.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Sets title.
		form.setText("Custom Form Widgets Demo");
		form.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		List<FormLayout> formList = getOctaneForms();
		Map<Entity, FormLayout> mapOfForms = formList.stream().filter(form -> form.isDefault())
				.collect(Collectors.toMap(FormLayout::getEntity, Function.identity()));

		FormLayout defectForm = mapOfForms.get(Entity.DEFECT);
		EntityModel defectData = getEntityWithId(Entity.DEFECT, 1022l);
		for (FormLayoutSection formSection : defectForm.getFormLayoutSections()) {
			demoSections(formSection, defectData);
		}
	}

	private void demoSections(FormLayoutSection formSection, EntityModel defectData) {
		form.getBody().setLayout(new TableWrapLayout());

		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);

		section.setText(formSection.getSectionTitle());
		toolkit.createCompositeSeparator(section);

		Composite sectionClient = new Composite(section, SWT.NONE);
		sectionClient.setLayout(new GridLayout(4, true));
		for (FormField formField : formSection.getFields()) {
			Label tempLabel = new Label(sectionClient, SWT.NONE);
			tempLabel.setText(prettifyLables(formField.getName()));

			Label tempValuesLabel = new Label(sectionClient, SWT.NONE);
			tempValuesLabel.setText(Util.getUiDataFromModel(defectData.getValue(formField.getName())));
			// Text tempValuesLabel = new Text(sectionClient, SWT.BORDER);
			// tempValuesLabel.setText(Util.getUiDataFromModel(defectData.getValue(formField.getName())));

		}
		section.setClient(sectionClient);
	}

	private List<FormLayout> getOctaneForms() {
		List<FormLayout> ret = null;
		ServiceModule serviceModule = new ServiceModule(new ConnectionSettingsProvider() {
			@Override
			public void setConnectionSettings(ConnectionSettings connectionSettings) {
			}

			@Override
			public ConnectionSettings getConnectionSettings() {
				ConnectionSettings connectionSettings = new ConnectionSettings();
				connectionSettings.setBaseUrl("http://myd-vm19852.hpeswlab.net:8080");
				connectionSettings.setSharedSpaceId(1001L);
				connectionSettings.setWorkspaceId(1004L);
				connectionSettings.setUserName("sa@nga");
				connectionSettings.setPassword("Welcome1");

				// TODO Auto-generated method stub
				return connectionSettings;
			}

			@Override
			public void addChangeHandler(Runnable observer) {
			}
		});

		try {
			ret = serviceModule.getInstance(MetadataService.class).getFormLayoutForAllEntityTypes();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private EntityModel getEntityWithId(Entity entityType, Long entityId) {
		EntityModel ret = null;
		ServiceModule serviceModule = new ServiceModule(new ConnectionSettingsProvider() {
			@Override
			public void setConnectionSettings(ConnectionSettings connectionSettings) {
			}

			@Override
			public ConnectionSettings getConnectionSettings() {
				ConnectionSettings connectionSettings = new ConnectionSettings();
				connectionSettings.setBaseUrl("http://myd-vm19852.hpeswlab.net:8080");
				connectionSettings.setSharedSpaceId(1001L);
				connectionSettings.setWorkspaceId(1004L);
				connectionSettings.setUserName("sa@nga");
				connectionSettings.setPassword("Welcome1");

				// TODO Auto-generated method stub
				return connectionSettings;
			}

			@Override
			public void addChangeHandler(Runnable observer) {
			}
		});

		try {
			ret = serviceModule.getInstance(EntityService.class).findEntity(entityType, entityId);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private String prettifyLables(String str1) {
		str1 = str1.replaceAll("_", " ");
		char[] chars = str1.toCharArray();

		// all ways make first char a cap
		chars[0] = Character.toUpperCase(chars[0]);

		// then capitalize if space on left.
		for (int x = 1; x < chars.length; x++) {
			if (chars[x - 1] == ' ') {
				chars[x] = Character.toUpperCase(chars[x]);
			}
		}

		return new String(chars);
	}

}