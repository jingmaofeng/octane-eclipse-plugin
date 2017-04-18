package com.hpe.octane.ideplugins.eclipse.ui.editor;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormField;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;

public class EntityModelEditor extends EditorPart {

	public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.EntityModelEditor"; //$NON-NLS-1$

	private EntityModel entityModel;
	private EntityModelEditorInput input;
	private Map<Entity, FormLayout> octaneForms;

	private EntityService entityService = Activator.getInstance(EntityService.class);
	private MetadataService metadataService = Activator.getInstance(MetadataService.class);

	private static EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);

	public EntityModelEditor() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof EntityModelEditorInput)) {
			throw new RuntimeException("Wrong input");
		}

		this.input = (EntityModelEditorInput) input;
		setSite(site);
		setInput(input);

		try {
			entityModel = entityService.findEntity(this.input.getEntityType(), this.input.getId());
			octaneForms = metadataService.getFormLayoutForAllEntityTypes();
		} catch (ServiceException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		setPartName(entityModel.getValue("name").getValue().toString());
		setTitleImage(entityIconFactory.getImageIcon(this.input.getEntityType()));
	}

	/**
	 * Create contents of the editor part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(4, true));

		if (entityModel != null) {
			FormLayout entityForm = octaneForms.get(Entity.getEntityType(entityModel));
			for (FormLayoutSection formSection : entityForm.getFormLayoutSections()) {
				for (FormField formField : formSection.getFields()) {
					Label tempLabel = new Label(composite, SWT.NONE);
					tempLabel.setText(prettifyLables(formField.getName()));

					Label tempValuesLabel = new Label(composite, SWT.NONE);
					tempValuesLabel.setText(Util.getUiDataFromModel(entityModel.getValue(formField.getName())));

				}
			}	
		}

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}