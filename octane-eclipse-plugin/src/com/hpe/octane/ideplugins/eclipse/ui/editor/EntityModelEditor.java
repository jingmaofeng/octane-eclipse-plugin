package com.hpe.octane.ideplugins.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.ui.part.EditorPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;
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
    private Form parentSectionForm;
    private FormToolkit sectionFormGenerator;
    private Collection<EntityModel> possibleTransitions;
    private LoadingComposite loadingComposite;
    private static String DESCRIPTION_FIELD = "description";
    private static EntityIconFactory entityIconFactoryForTabInfo = new EntityIconFactory(20, 20, 7);
    private static EntityIconFactory entityIconFactory = new EntityIconFactory(25, 25, 7);
    private Composite headerAndEntityDetailsParent;
    private boolean shouldShowPhase = true;
    private ScrolledComposite headerAndEntityDetailsScrollComposite;
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
        setTitleImage(entityIconFactoryForTabInfo.getImageIcon(this.input.getEntityType()));
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        parent.setBackgroundMode(SWT.INHERIT_FORCE);

        StackLayoutComposite stackContainer = new StackLayoutComposite(parent, SWT.NONE);

        // set loading GIF until the data is loaded
        loadingComposite = new LoadingComposite(stackContainer, SWT.NONE);
        stackContainer.showControl(loadingComposite);

        // This job retrieves the necessary data for the details view
        getEntiyJob = new GetEntityDetailsJob("Retiving entity details", this.input.getEntityType(), this.input.getId());
        getEntiyJob.schedule();
        getEntiyJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    stackContainer.showControl(loadingComposite);
                });
            }

            @Override
            public void done(IJobChangeEvent event) {
                if (getEntiyJob.wasEntityRetrived()) {
                    entityModel = getEntiyJob.getEntiyData();
                    Display.getDefault().asyncExec(() -> {
                        entityModel = getEntiyJob.getEntiyData();
                        octaneEntityForm = getEntiyJob.getFormForCurrentEntity();
                        if (getEntiyJob.shouldShowPhase()) {
                            shouldShowPhase = true;
                            currentPhase = getEntiyJob.getCurrentPhase();
                            possibleTransitions = getEntiyJob.getPossibleTransitionsForCurrentEntity();
                        } else {
                            shouldShowPhase = false;
                        }
                        // After the data is loaded the UI is created
                        createSpecificEntitySections(stackContainer);
                        // After the UI is created it gets displayed
                        stackContainer.showControl(headerAndEntityDetailsScrollComposite);
                    });
                }
            }
        });
    }

    private void createSpecificEntitySections(Composite parent) {
        headerAndEntityDetailsScrollComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        headerAndEntityDetailsScrollComposite.setExpandHorizontal(true);
        headerAndEntityDetailsScrollComposite.setExpandVertical(true);

        headerAndEntityDetailsParent = new Composite(headerAndEntityDetailsScrollComposite, SWT.NONE);
        headerAndEntityDetailsParent.setLayout(new FillLayout(SWT.HORIZONTAL));
        headerAndEntityDetailsParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        createHeaderPanel(headerAndEntityDetailsParent);

        sectionFormGenerator = new FormToolkit(parent.getDisplay());

        parentSectionForm = sectionFormGenerator.createForm(headerAndEntityDetailsParent);
        parentSectionForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        parentSectionForm.getBody().setLayout(new GridLayout(1, false));
        parentSectionForm.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        sectionFormGenerator.adapt(headerAndEntityDetailsParent);
        sectionFormGenerator.paintBordersFor(headerAndEntityDetailsParent);

        if (entityModel != null) {
            // For each section form from Octane one is created in eclipse.
            for (FormLayoutSection formSection : octaneEntityForm.getFormLayoutSections()) {
                createSectionsWithEntityData(formSection);
            }
            // For the Description a separate section is created
            createDescriptionFormSection();
        }
        headerAndEntityDetailsScrollComposite.setContent(headerAndEntityDetailsParent);
        headerAndEntityDetailsScrollComposite.setMinSize(headerAndEntityDetailsParent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void createHeaderPanel(Composite parent) {
        headerAndEntityDetailsParent.setLayout(new GridLayout(1, false));
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
        Font boldFont = new Font(lblEntityName.getDisplay(), new FontData(JFaceResources.DEFAULT_FONT, 12, SWT.BOLD));
        lblEntityName.setFont(boldFont);

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

            Button savePhase = new Button(genericHeaderComposite, SWT.NONE);
            savePhase.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));
            savePhase.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    saveCurrentPhase();
                }
            });
        }
        Button refresh = new Button(genericHeaderComposite, SWT.NONE);
        refresh.setImage(ImageResources.REFRESH_16X16.getImage());
        refresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                getEntiyJob.schedule();
            }
        });
    }

    // STEP 3
    private void createDescriptionFormSection() {

        Section section = sectionFormGenerator.createSection(parentSectionForm.getBody(),
                Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                1));
        section.setExpanded(true);

        section.setLayout(new FillLayout(SWT.HORIZONTAL));
        section.setText("Description");

        Browser descriptionPanel = new Browser(section, SWT.NONE);
        String descriptionText = Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION));
        if (descriptionText.isEmpty()) {
            descriptionPanel.setText("No description");
        } else {
            descriptionPanel.setText(descriptionText);
        }
        sectionFormGenerator.createCompositeSeparator(section);
        section.setClient(descriptionPanel);
    }

    // STEP 4
    private void createSectionsWithEntityData(FormLayoutSection formSection) {
        Section section = sectionFormGenerator.createSection(parentSectionForm.getBody(),
                Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);
        section.setText(formSection.getSectionTitle());
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        section.setExpanded(true);
        sectionFormGenerator.createCompositeSeparator(section);

        Composite sectionClient = new Composite(section, SWT.NONE);
        sectionClient.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite sectionClientLeft = new Composite(sectionClient, SWT.NONE);
        sectionClientLeft.setLayout(new GridLayout(2, false));
        Composite sectionClientRight = new Composite(sectionClient, SWT.NONE);
        sectionClientRight.setLayout(new GridLayout(2, false));
        for (int i = 0; i <= formSection.getFields().size() - 1; i += 2) {
            if (!DESCRIPTION_FIELD.equals(formSection.getFields().get(i).getName())) {
                CLabel tempLabelLeft = new CLabel(sectionClientLeft, SWT.NONE);
                tempLabelLeft.setText(prettifyLabels(formSection.getFields().get(i).getName()));
                tempLabelLeft.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
                tempLabelLeft.setMargins(5, 2, 5, 2);

                CLabel tempValuesLabelLeft = new CLabel(sectionClientLeft, SWT.NONE);
                tempValuesLabelLeft.setText(Util.getUiDataFromModel(entityModel.getValue(formSection.getFields().get(i).getName())));
                tempValuesLabelLeft.setMargins(5, 2, 5, 2);
            }
            if (formSection.getFields().size() > i + 1 && null != formSection.getFields().get(i + 1)
                    && !DESCRIPTION_FIELD.equals(formSection.getFields().get(i + 1).getName())) {
                CLabel tempLabelRight = new CLabel(sectionClientRight, SWT.NONE);
                tempLabelRight.setText(prettifyLabels(formSection.getFields().get(i + 1).getName()));
                tempLabelRight.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
                tempLabelRight.setMargins(5, 2, 5, 2);

                CLabel tempValuesLabelRight = new CLabel(sectionClientRight, SWT.NONE);
                tempValuesLabelRight.setText(Util.getUiDataFromModel(entityModel.getValue(formSection.getFields().get(i + 1).getName())));
                tempValuesLabelRight.setMargins(5, 2, 5, 2);
            }
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
        ChangePhaseJob changePhaseJob = new ChangePhaseJob("Chaging phase of entity", entityModel, selectedPhase);
        changePhaseJob.schedule();
        changePhaseJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    if (changePhaseJob.isPhaseChanged()) {
                        new InfoPopup("Phase Transition", "Phase was changed").open();
                    } else {
                        MessageDialog.openError(Display.getCurrent().getActiveShell(), "ERROR",
                                "Phase changed failed \n " + changePhaseJob.getFailedReason());
                    }
                    getEntiyJob.schedule();
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