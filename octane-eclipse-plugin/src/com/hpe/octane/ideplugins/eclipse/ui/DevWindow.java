package com.hpe.octane.ideplugins.eclipse.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.LongFieldModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.filter.UserItemArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.AbsoluteLayoutEntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.mywork.rowrenderer.MyWorkEntityModelRowRenderer;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class DevWindow {

    protected Shell shell;
    EntityIconFactory iconFactory = new EntityIconFactory();

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            DevWindow window = new DevWindow();
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

        shell.addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                System.exit(0);
            }
        });

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
        shell.setSize(450, 300);
        shell.setText("SWT Application");
        shell.setLayout(new FillLayout());
        shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        EntityListData data = new UserItemArrayEntityListData();

        Collection<EntityModel> entities = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            EntityModel em = new EntityModel();

            EntityModel def = new EntityModel();
            def.setValue(new StringFieldModel("type", "work_item"));
            def.setValue(new StringFieldModel("subtype", "defect"));
            def.setValue(new StringFieldModel("name", "HelpMe"));
            def.setValue(new StringFieldModel("id", i + ""));

            em.setValue(new StringFieldModel("id", i + ""));
            em.setValue(new StringFieldModel("type", "user_item"));
            em.setValue(new ReferenceFieldModel("my_follow_items_work_item", def));
            em.setValue(new StringFieldModel("entity_type", "work_item"));
            em.setValue(new LongFieldModel("origin", 1L));

            entities.add(em);
        }

        EntityListComposite entityListComposite = new EntityListComposite(
                shell,
                SWT.NONE,
                data,
                (viewerParent) -> {
                    EntityListViewer viewer = new AbsoluteLayoutEntityListViewer((Composite) viewerParent,
                            SWT.BORDER,
                            new MyWorkEntityModelRowRenderer(),
                            new EntityModelMenuFactory() {
                                @Override
                                public Menu createMenu(EntityModel entityModel, Control menuParent) {
                                    // TODO Auto-generated method stub
                                    return null;
                                }
                            });
                    return viewer;
                });

        data.setEntityList(entities);

    }
}
