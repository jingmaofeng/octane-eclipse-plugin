package com.hpe.octane.ideplugins.eclipse.ui.editor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormField;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBoxLabelProvider;
import com.hpe.octane.ideplugins.eclipse.ui.editor.job.ChangePhaseJob;
import com.hpe.octane.ideplugins.eclipse.ui.editor.job.GetEntityDetailsJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.InfoPopup;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class EntityModelEditor extends EditorPart {

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.EntityModelEditor"; //$NON-NLS-1$

    private EntityModel entityModel;
    private FieldModel currentPhase;
    private EntityModel selectedPhase;
    private EntityModelEditorInput input;
    private FormLayout octaneEntityForm;
    private Form specificEntityDetails;
    private FormToolkit toolkit;
    private Collection<EntityModel> possibleTransitions;

    private EntityService entityService = Activator.getInstance(EntityService.class);
    private MetadataService metadataService = Activator.getInstance(MetadataService.class);

    private static EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);
    private Composite entityDetailsComposite;

    private boolean shouldShowPhase = true;

    private ScrolledComposite entityDetailsScrolledComposite;

    private GetEntityDetailsJob getEntiyJob;

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

        setPartName(String.valueOf(this.input.getId()));
        setTitleImage(entityIconFactory.getImageIcon(this.input.getEntityType()));
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        StackLayoutComposite container = new StackLayoutComposite(parent,
                SWT.NONE);

        LoadingComposite loadingComposite = new LoadingComposite(container,
                SWT.NONE);
        container.showControl(loadingComposite);

        getEntiyJob = new GetEntityDetailsJob("Retiving entity details", this.input.getEntityType(), this.input.getId());
        getEntiyJob.schedule();
        getEntiyJob.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    container.showControl(loadingComposite);
                });
            }

            @Override
            public void done(IJobChangeEvent event) {
                if (getEntiyJob.wasEntityRetrived()) {
                    entityModel = getEntiyJob.getEntiyData();

                    Display.getDefault().asyncExec(() -> {
                        try {
                            octaneEntityForm = metadataService.getFormLayoutForSpecificEntityType(Entity.getEntityType(entityModel));
                            currentPhase = entityModel.getValue("phase");
                            Long currentPhaseId = Long.valueOf(Util.getUiDataFromModel(currentPhase, "id"));
                            possibleTransitions = entityService.findPossibleTransitionFromCurrentPhase(Entity.getEntityType(entityModel),
                                    currentPhaseId);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        createSpecificEntitySections(container);
                        container.showControl(entityDetailsScrolledComposite);
                    });
                }

            }
        });

    }

    private void createSpecificEntitySections(Composite parent) {
        entityDetailsScrolledComposite = new ScrolledComposite(parent,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        entityDetailsScrolledComposite.setExpandHorizontal(true);
        entityDetailsScrolledComposite.setExpandVertical(true);

        entityDetailsComposite = new Composite(entityDetailsScrolledComposite, SWT.NONE);

        createGeneralEntitySection(entityDetailsComposite);

        toolkit = new FormToolkit(parent.getDisplay());
        specificEntityDetails = toolkit.createForm(entityDetailsComposite);
        specificEntityDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        if (entityModel != null) {
            for (FormLayoutSection formSection : octaneEntityForm.getFormLayoutSections()) {
                addEntityDataToSection(entityModel, formSection);
            }
        }
        entityDetailsScrolledComposite.setContent(entityDetailsComposite);
        entityDetailsScrolledComposite.setMinSize(entityDetailsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
                selectedPhase = newSelection;
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
        savePhase.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                saveCurrentPhase();
            }
        });

        Button refresh = new Button(genericHeaderComposite, SWT.NONE);
        refresh.setImage(ImageResources.REFRESH_16X16.getImage());
        refresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                getEntiyJob.schedule();
            }
        });
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
            tempLabel.setText(prettifyLabels(formField.getName()));

            Label tempValuesLabel = new Label(sectionClient, SWT.NONE);
            tempValuesLabel.setText(Util.getUiDataFromModel(entityModel.getValue(formField.getName())));
        }
        section.setClient(sectionClient);
    }

    private String prettifyLabels(String str1) {
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

    private void saveCurrentPhase() {
        ChangePhaseJob changePhaseHob = new ChangePhaseJob("Chaging phase of entity", entityModel, selectedPhase);
        changePhaseHob.schedule();
        changePhaseHob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    if (changePhaseHob.isPhaseChanged()) {
                        new InfoPopup("Phase Transition", "Phase was changed").open();
                        // TODO: osavencu: refresh entity
                    } else {
                        // TODO:osavencu: open error dialog
                    }
                });
            }
        });
    }

    private void setEntiyData() {
        GetEntityDetailsJob getEntiyJob = new GetEntityDetailsJob("Retiving entity details", this.input.getEntityType(), this.input.getId());
        getEntiyJob.schedule();
        getEntiyJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    if (getEntiyJob.wasEntityRetrived()) {
                        entityModel = getEntiyJob.getEntiyData();
                    }
                });
            }
        });
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