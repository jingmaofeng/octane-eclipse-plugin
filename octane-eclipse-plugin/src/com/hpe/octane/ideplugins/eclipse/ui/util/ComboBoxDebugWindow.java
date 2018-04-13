package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.connection.BasicConnectionSettingProvider;
import com.hpe.adm.octane.ideplugins.services.connection.ConnectionSettings;
import com.hpe.adm.octane.ideplugins.services.di.ServiceModule;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.EntityComboBox.EntityLoader;

public class ComboBoxDebugWindow {

    protected Shell shell;
    protected Display display;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            ComboBoxDebugWindow window = new ComboBoxDebugWindow();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        display = Display.getDefault();
        try {
            shell = new Shell(display);
            try {
                createContents();
                shell.open();
                while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
            } finally {
                if (!shell.isDisposed()) {
                    shell.dispose();
                }
            }
        } finally {
            display.dispose();
        }
        System.exit(0);
    }


    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(450, 300);
        shell.setText("SWT Application");
        shell.setLayout(new GridLayout(1, false));
        
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setBaseUrl("http://octane.ide.octane.work");
        connectionSettings.setSharedSpaceId(1001L);
        connectionSettings.setWorkspaceId(1002L);
        connectionSettings.setUserName("sa@nga");
        connectionSettings.setPassword("Welcome1");
        
        ServiceModule serviceModule = new ServiceModule(new BasicConnectionSettingProvider(connectionSettings)); 
        EntityService entityService = serviceModule.getInstance(EntityService.class);
        
        LabelProvider lblProvider = new LabelProvider() {
            public String getText(Object element) {
                return Util.getUiDataFromModel((new ReferenceFieldModel("", (EntityModel) element)));
            }
        };
        
        EntityLoader entityLoader = new EntityLoader() {
            @Override
            public List<EntityModel> loadEntities(String searchQuery) {
                return new ArrayList<>(entityService.findEntities(Entity.DEFECT));
            }
        };

        EntityComboBox entityComboBox = new EntityComboBox(shell, SWT.MULTI, lblProvider, entityLoader);
    }
}
