package com.hpe.octane.ideplugins.eclipse.ui.editor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wb.swt.SWTResourceManager;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormField;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBoxLabelProvider;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class EntityModelEditor extends EditorPart {

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.EntityModelEditor"; //$NON-NLS-1$

    private EntityModel entityModel;
    private FieldModel currentPhase;
    private EntityModel selectedPhase;
    private EntityModelEditorInput input;
    private Map<Entity, FormLayout> octaneForms;
    private Form specificEntityDetails;
    private FormToolkit toolkit;
    private Collection<EntityModel> possibleTransitions;

    private EntityService entityService = Activator.getInstance(EntityService.class);
    private MetadataService metadataService = Activator.getInstance(MetadataService.class);

    private static EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);
    private Composite entityDetailsComposite;

    private boolean shouldShowPhase = true;

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
            currentPhase = entityModel.getValue("phase");
            Long currentPhaseId = Long.valueOf(Util.getUiDataFromModel(currentPhase, "id"));
            possibleTransitions = entityService.findPossibleTransitionFromCurrentPhase(Entity.getEntityType(entityModel), currentPhaseId);
        } catch (ServiceException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setPartName(Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_ID)));
        setTitleImage(entityIconFactory.getImageIcon(this.input.getEntityType()));
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        createSpecificEntitySections(parent);
    }

    private void createGeneralEntitySection(Composite parent) {
        entityDetailsComposite.setLayout(new GridLayout(1, false));
        Composite genericHeaderComposite = new Composite(parent, SWT.NONE);
        genericHeaderComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
        genericHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        genericHeaderComposite.setLayout(new GridLayout(6, false));

        Label entityIcon = new Label(genericHeaderComposite, SWT.NONE);
        entityIcon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        entityIcon.setImage(entityIconFactory.getImageIcon(Entity.getEntityType(entityModel)));

        Label lblEntityName = new Label(genericHeaderComposite, SWT.NONE);
        lblEntityName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
        lblEntityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblEntityName.setText(Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_NAME)));

        if (shouldShowPhase) {
            Label lblCurrentPhase = new Label(genericHeaderComposite, SWT.NONE);
            lblCurrentPhase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            lblCurrentPhase.setText(Util.getUiDataFromModel(currentPhase, EntityFieldsConstants.FIELD_NAME));

            CustomEntityComboBox<EntityModel> nextPhasesComboBox = new CustomEntityComboBox<EntityModel>(genericHeaderComposite);
            nextPhasesComboBox.addSelectionListener((phaseEntityModel, newSelection) -> {
                System.out.println(newSelection);
                // TODO: save this value
            });
            nextPhasesComboBox.setLabelProvider(new CustomEntityComboBoxLabelProvider<EntityModel>() {

                @Override
                public String getSelectedLabel(EntityModel entityModelElement) {
                    return Util.getUiDataFromModel(entityModelElement.getValue("target_phase"), "name");
                }

                @Override
                public String getListLabel(EntityModel entityModelElement) {
                    return Util.getUiDataFromModel(entityModelElement.getValue("target_phase"), "name");
                }
            });

            nextPhasesComboBox.setContent(new ArrayList<>(possibleTransitions));

            nextPhasesComboBox.selectFirstItem();
        }
        Button savePhase = new Button(genericHeaderComposite, SWT.NONE);
        savePhase.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));
        Button refresh = new Button(genericHeaderComposite, SWT.NONE);
        refresh.setImage(ImageResources.REFRESH_16X16.getImage());
    }

    private void createSpecificEntitySections(Composite parent) {
        ScrolledComposite entityDetailsScrolledComposite = new ScrolledComposite(parent,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        entityDetailsScrolledComposite.setExpandHorizontal(true);
        entityDetailsScrolledComposite.setExpandVertical(true);

        entityDetailsComposite = new Composite(entityDetailsScrolledComposite, SWT.NONE);

        createGeneralEntitySection(entityDetailsComposite);

        toolkit = new FormToolkit(parent.getDisplay());
        specificEntityDetails = toolkit.createForm(entityDetailsComposite);
        specificEntityDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        if (entityModel != null) {
            FormLayout entityForm = octaneForms.get(Entity.getEntityType(entityModel));
            for (FormLayoutSection formSection : entityForm.getFormLayoutSections()) {
                addEntityDataToSection(entityModel, formSection);
            }
        }
        entityDetailsScrolledComposite.setContent(entityDetailsComposite);
        entityDetailsScrolledComposite.setMinSize(entityDetailsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void addEntityDataToSection(EntityModel entityModel, FormLayoutSection formSection) {
        specificEntityDetails.getBody().setLayout(new TableWrapLayout());

        Section section = toolkit.createSection(specificEntityDetails.getBody(),
                Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);

        section.setText(formSection.getSectionTitle());
        toolkit.createCompositeSeparator(section);

        Composite sectionClient = new Composite(section, SWT.NONE);
        sectionClient.setLayout(new GridLayout(4, true));
        for (FormField formField : formSection.getFields()) {
            Label tempLabel = new Label(sectionClient, SWT.NONE);
            tempLabel.setText(prettifyLables(formField.getName()));

            Label tempValuesLabel = new Label(sectionClient, SWT.NONE);
            tempValuesLabel.setText(Util.getUiDataFromModel(entityModel.getValue(formField.getName())));
        }
        section.setClient(sectionClient);
    }

    private String prettifyLables(String str1) {
        str1 = str1.replaceAll("_", " ");
        char[] chars = str1.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
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