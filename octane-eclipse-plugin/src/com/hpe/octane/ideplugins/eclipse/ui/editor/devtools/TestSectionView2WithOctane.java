package com.hpe.octane.ideplugins.eclipse.ui.editor.devtools;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hpe.adm.nga.sdk.model.EntityModel;
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

public class TestSectionView2WithOctane {
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
            TestSectionView2WithOctane window = new TestSectionView2WithOctane();
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

        Composite parent = new Composite(shell, SWT.NULL);
        parent.setLayout(new FillLayout());

        // Sets title.
        FormLayout defectForm = getOctaneForms(Entity.DEFECT);
        EntityModel defectData = getEntityWithId(Entity.DEFECT, 1138l);
        for (FormLayoutSection formSection : defectForm.getFormLayoutSections()) {
            demoSections(formSection, defectData, parent);
        }
    }

    private void demoSections(FormLayoutSection formSection, EntityModel defectData, Composite parent) {

        Composite sectionClientLeft = new Composite(parent, SWT.NONE);
        sectionClientLeft.setLayout(new GridLayout(2, false));

        Composite sectionClientRight = new Composite(parent, SWT.NONE);
        sectionClientRight.setLayout(new GridLayout(2, false));
        // formSection.setFields(formSection.getFields().subList(0, 19));
        for (int i = 0; i <= formSection.getFields().size() - 1; i += 2) {
            Label tempLabelLeft = new Label(sectionClientLeft, SWT.NONE);
            tempLabelLeft.setText(prettifyLables(formSection.getFields().get(i).getName()));

            Label tempValuesLabelLeft = new Label(sectionClientLeft, SWT.NONE);
            tempValuesLabelLeft.setText(Util.getUiDataFromModel(defectData.getValue(formSection.getFields().get(i).getName())));
            if (formSection.getFields().size() > i + 1 && null != formSection.getFields().get(i + 1)) {
                Label tempLabelRight = new Label(sectionClientRight, SWT.NONE);
                tempLabelRight.setText(prettifyLables(formSection.getFields().get(i + 1).getName()));

                Label tempValuesLabelRight = new Label(sectionClientRight, SWT.NONE);
                tempValuesLabelRight.setText(Util.getUiDataFromModel(defectData.getValue(formSection.getFields().get(i + 1).getName())));
            }

        }

        // for (FormField formField : formSection.getFields()) {
        // Label tempLabel = new Label(sectionClient, SWT.NONE);
        // tempLabel.setText(prettifyLables(formField.getName()));
        //
        // Label tempValuesLabel = new Label(sectionClient, SWT.NONE);
        // tempValuesLabel.setText(Util.getUiDataFromModel(defectData.getValue(formField.getName())));
        // }
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