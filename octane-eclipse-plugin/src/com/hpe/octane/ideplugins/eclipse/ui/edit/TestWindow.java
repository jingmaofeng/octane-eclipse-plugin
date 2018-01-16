package com.hpe.octane.ideplugins.eclipse.ui.edit;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.nga.sdk.Octane;
import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.ideplugins.services.connection.ConnectionSettings;
import com.hpe.adm.octane.ideplugins.services.di.ServiceModule;
import com.hpe.adm.octane.ideplugins.services.exception.ServiceException;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;

public class TestWindow {

	protected Shell shell;
	private EntityModel entityModel;
	private Map<String, String> prettyFieldsMap;

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
		shell.setSize(819, 579);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));

		Button btnSave = new Button(composite, SWT.NONE);
		GridData gd_btnSave = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSave.widthHint = 164;
		btnSave.setLayoutData(gd_btnSave);
		btnSave.setText("Save");

		ConnectionSettings connectionSettings = new ConnectionSettings(
				"http://myd-vm10632.hpeswlab.net:8081", 
				1001L,
				1002L,
				"sa@nga",
				"Welcome1");

		ServiceModule serviceModule = new ServiceModule(new BasicConnectionSettingProvider(connectionSettings));

		EntityService entityService = serviceModule.getInstance(EntityService.class);
		MetadataService metaDataService = serviceModule.getInstance(MetadataService.class);
		Collection<FieldMetadata> fieldMeta = metaDataService.getFields(Entity.USER_STORY);
		


		try {
			entityModel = entityService.findEntity(Entity.USER_STORY, 1002L);
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}

		for ( FieldModel fieldModel : entityModel.getValues()) {
			FieldMetadata fieldMetadata = getMetaForModel(fieldMeta, fieldModel);

			if (fieldMetadata == null) {
				System.out.println("WTF " + fieldModel.getName() + " has no meta");
			} else {
				FieldModelEditor fieldModelEditor = new FieldModelEditor(composite, SWT.NONE, entityModel, fieldModel,
						getMetaForModel(fieldMeta, fieldModel));
				fieldModelEditor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			}
		}

		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Octane octane = serviceModule.getOctane().getOctane();
				octane.entityList(Entity.USER_STORY.getApiEntityName()).at("1002").update().entity(entityModel)
						.execute();
			}
		});

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private static FieldMetadata getMetaForModel(Collection<FieldMetadata> metaList, FieldModel fieldModel) {
		Optional<FieldMetadata> op = metaList.stream().filter(meta -> meta.getName().equals(fieldModel.getName()))
				.findFirst();
		if (op.isPresent()) {
			return op.get();
		} else {
			System.err.println(fieldModel.getName() + " has no meta");
			return null;
		}

	}

}
