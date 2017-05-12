package com.hpe.octane.ideplugins.eclipse.ui.editor.devtools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.ConnectionSettingsProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.ui.FormLayoutSection;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBoxLabelProvider;
import com.hpe.octane.ideplugins.eclipse.ui.editor.job.GetEntityDetailsJob;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class DetailsView {
    private Composite headerAndEntityDetailsParent;
    private EntityModel entityModel;
    private FormToolkit sectionFormGenerator;
    private Form parentSectionForm;
    private FormLayout octaneEntityForm;
    private FieldModel currentPhase;
    private EntityModel selectedPhase;
    private GetEntityDetailsJob getEntiyJob;
    private Collection<EntityModel> possibleTransitions;
    private ScrolledComposite headerAndEntityDetailsScrollComposite;
    protected Shell shell;

    private static EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);
    private Composite formParent;
    private Text text;

    public static void main(String[] args) {
        try {
            DetailsView window = new DetailsView();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        Display display = Display.getDefault();
        entityModel = getEntityWithId(Entity.DEFECT, 1138l);
        octaneEntityForm = getOctaneForms(Entity.DEFECT);
        possibleTransitions = new ArrayList<>();
        currentPhase = new StringFieldModel("target_phase", "Open");
        possibleTransitions.add(new EntityModel("target_phase", "Open"));
        possibleTransitions.add(new EntityModel("target_phase", "Defered"));
        possibleTransitions.add(new EntityModel("target_phase", "Duplicated"));
        createSpecificEntitySections();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private FormLayout getOctaneForms(Entity entityType) {
        FormLayout ret = null;
        ServiceModule serviceModule = new ServiceModule(new ConnectionSettingsProvider() {
            @Override
            public void setConnectionSettings(ConnectionSettings connectionSettings) {
            }

            @Override
            public ConnectionSettings getConnectionSettings() {
                ConnectionSettings connectionSettings = new ConnectionSettings();
                connectionSettings.setBaseUrl("http://myd-vm19852.hpeswlab.net:8080");
                connectionSettings.setSharedSpaceId(1001L);
                connectionSettings.setWorkspaceId(1002L);
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
            ret = serviceModule.getInstance(MetadataService.class).getFormLayoutForSpecificEntityType(entityType);
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
                connectionSettings.setWorkspaceId(1002L);
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

    // STEP 1
    private void createSpecificEntitySections() {
        shell = new Shell();
        shell.setSize(487, 300);
        shell.setText("SWT Application");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        shell.setBackgroundMode(SWT.INHERIT_FORCE);

        headerAndEntityDetailsScrollComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        headerAndEntityDetailsScrollComposite.setExpandHorizontal(true);
        headerAndEntityDetailsScrollComposite.setExpandVertical(true);

        headerAndEntityDetailsParent = new Composite(headerAndEntityDetailsScrollComposite, SWT.NONE);

        createGeneralEntitySection(headerAndEntityDetailsParent);

        sectionFormGenerator = new FormToolkit(shell.getDisplay());

        Composite detailsAndCommentsComposite = new Composite(headerAndEntityDetailsParent, SWT.NONE);
        detailsAndCommentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sectionFormGenerator.adapt(detailsAndCommentsComposite);
        sectionFormGenerator.paintBordersFor(detailsAndCommentsComposite);
        detailsAndCommentsComposite.setLayout(new GridLayout(2, false));

        formParent = new Composite(detailsAndCommentsComposite, SWT.NONE);
        formParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        formParent.setLayout(new FillLayout(SWT.HORIZONTAL));
        sectionFormGenerator.adapt(formParent);
        sectionFormGenerator.paintBordersFor(formParent);

        parentSectionForm = sectionFormGenerator.createForm(formParent);
        parentSectionForm.getBody().setLayout(new GridLayout(1, false));

        Composite composite = new Composite(detailsAndCommentsComposite, SWT.NONE);
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gd_composite.widthHint = 300;
        gd_composite.minimumWidth = 300;
        composite.setLayoutData(gd_composite);
        sectionFormGenerator.adapt(composite);
        sectionFormGenerator.paintBordersFor(composite);
        composite.setLayout(new GridLayout(1, false));

        text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        sectionFormGenerator.adapt(text, true, true);

        Button btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        sectionFormGenerator.adapt(btnNewButton, true, true);
        btnNewButton.setText("Send");

        Browser browser = new Browser(composite, SWT.NONE);
        browser.setText("dasdas asdgf asgafe gadfg adfg adfg adfg adfg adf gadfg adfg afg afg asfg afg afg agagegtq345 4hq24h 1q45 q34trq5bwrb");
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sectionFormGenerator.adapt(browser);
        sectionFormGenerator.paintBordersFor(browser);

        if (entityModel != null) {

            // create other Sections
            for (FormLayoutSection formSection : octaneEntityForm.getFormLayoutSections()) {
                createOctaneFormSectionsWithData(entityModel, formSection);
            }

            // Create Description Section
            createDescriptionFormSection(entityModel);

        }

        headerAndEntityDetailsScrollComposite.setContent(headerAndEntityDetailsParent);
        headerAndEntityDetailsScrollComposite.setMinSize(headerAndEntityDetailsParent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    // STEP 2 - this creates the header
    /**
     * @param parent
     */
    private void createGeneralEntitySection(Composite parent) {
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
        savePhase.setImage(ImageResources.REFRESH_16X16.getImage());
        savePhase.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                // saveCurrentPhase();
            }
        });

        Button refresh = new Button(genericHeaderComposite, SWT.NONE);
        refresh.setImage(ImageResources.REFRESH_16X16.getImage());
        new Label(genericHeaderComposite, SWT.NONE);
        refresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                getEntiyJob.schedule();
            }
        });

    }

    // STEP 3 - this creates description
    private void createDescriptionFormSection(EntityModel entityModel2) {

        Section section = sectionFormGenerator.createSection(parentSectionForm.getBody(), Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        section.setExpanded(true);

        section.setLayout(new FillLayout(SWT.HORIZONTAL));
        section.setText("Description");

        Browser descriptionPanel = new Browser(section, SWT.NONE);
        descriptionPanel.setText(Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION)));
        sectionFormGenerator.createCompositeSeparator(section);
        section.setClient(descriptionPanel);
    }

    // STEP 4 - this creates every section with its fields
    private void createOctaneFormSectionsWithData(EntityModel entityModel, FormLayoutSection formSection) {
        Section section = sectionFormGenerator.createSection(parentSectionForm.getBody(), Section.DESCRIPTION | Section.TREE_NODE | Section.EXPANDED);
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        section.setExpanded(true);

        section.setLayout(new FillLayout(SWT.HORIZONTAL));
        section.setText(formSection.getSectionTitle());
        sectionFormGenerator.createCompositeSeparator(section);

        Composite sectionClient = new Composite(section, SWT.NONE);
        sectionClient.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite sectionClientLeft = new Composite(sectionClient, SWT.NONE);
        sectionClientLeft.setLayout(new GridLayout(2, false));
        Composite sectionClientRight = new Composite(sectionClient, SWT.NONE);
        sectionClientRight.setLayout(new GridLayout(2, false));
        for (int i = 0; i <= formSection.getFields().size() - 1; i += 2) {
            if (!"description".equals(formSection.getFields().get(i).getName())) {
                Label tempLabelLeft = new Label(sectionClientLeft, SWT.NONE);
                tempLabelLeft.setText(prettifyLabels(formSection.getFields().get(i).getName()));
                tempLabelLeft.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

                Label tempValuesLabelLeft = new Label(sectionClientLeft, SWT.NONE);
                tempValuesLabelLeft.setText(Util.getUiDataFromModel(entityModel.getValue(formSection.getFields().get(i).getName())));
            }
            if (formSection.getFields().size() > i + 1 && null != formSection.getFields().get(i + 1)
                    && !"description".equals(formSection.getFields().get(i + 1).getName())) {
                Label tempLabelRight = new Label(sectionClientRight, SWT.NONE);
                tempLabelRight.setText(prettifyLabels(formSection.getFields().get(i + 1).getName()));
                tempLabelRight.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

                Label tempValuesLabelRight = new Label(sectionClientRight, SWT.NONE);
                tempValuesLabelRight.setText(Util.getUiDataFromModel(entityModel.getValue(formSection.getFields().get(i + 1).getName())));
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

    public DetailsView() {

    }
}
