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

        Composite sectionClient = new Composite(parent, SWT.NONE);
        sectionClient.setLayout(new GridLayout(4, true));
        for (FormField formField : formSection.getFields()) {
            Label tempLabel = new Label(sectionClient, SWT.NONE);
            tempLabel.setText(prettifyLables(formField.getName()));

            Label tempValuesLabel = new Label(sectionClient, SWT.NONE);
            tempValuesLabel.setText(Util.getUiDataFromModel(defectData.getValue(formField.getName())));
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