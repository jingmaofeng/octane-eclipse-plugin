package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import static com.hpe.adm.octane.services.util.Util.getUiDataFromModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;
import com.hpe.octane.ideplugins.eclipse.util.DebugUtil;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class MyWorkEntityModelMenuFactory implements EntityModelMenuFactory {

    // private static final ILog logger = Activator.getDefault().getLog();

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);
    private static EntityService entityService = DebugUtil.serviceModule.getInstance(EntityService.class);
    private static MyWorkService myWorkService = DebugUtil.serviceModule.getInstance(MyWorkService.class);
    private ViewPart parentViewPart;

    public MyWorkEntityModelMenuFactory(ViewPart parentViewPart) {
        this.parentViewPart = parentViewPart;
    }

    private void openDetailTab(Integer entityId, Entity entityType) {
        EntityModelEditorInput entityModelEditorInput = new EntityModelEditorInput(entityId, entityType);
        try {
            parentViewPart.getSite().getWorkbenchWindow().getActivePage().openEditor(entityModelEditorInput, EntityModelEditor.ID);
        } catch (PartInitException ex) {
            // logger.log(new Status(Status.ERROR, Activator.PLUGIN_ID,
            // Status.ERROR, "An exception has occured when opening the editor",
            // ex));
        }
    }

    @Override
    public Menu createMenu(EntityModel userItem, Control menuParent) {

        Menu menu = new Menu(menuParent);

        EntityModel entityModel = MyWorkUtil.getEntityModelFromUserItem(userItem);
        Entity entityType = Entity.getEntityType(entityModel);
        // String entityName =
        // Util.getUiDataFromModel(entityModel.getValue("name"));
        Integer entityId = Integer.valueOf(getUiDataFromModel(entityModel.getValue("id")));

        addMenuItem(
                menu,
                "View in browser",
                ImageResources.BROWSER_16X16.getImage(),
                () -> entityService.openInBrowser(entityModel));

        if (entityType != Entity.COMMENT) {
            addMenuItem(
                    menu,
                    "View details",
                    entityIconFactory.getImageIcon(entityType),
                    () -> openDetailTab(entityId, entityType));
        }

        if (entityType == Entity.TASK || entityType == Entity.COMMENT) {
            // Get parent info
            EntityModel parentEntityModel;
            if (entityType == Entity.TASK) {
                parentEntityModel = (EntityModel) entityModel.getValue("story").getValue();
            } else {
                parentEntityModel = (EntityModel) Util.getContainerItemForCommentModel(entityModel).getValue();
            }

            addMenuItem(
                    menu,
                    "View parent details",
                    entityIconFactory.getImageIcon(Entity.getEntityType(parentEntityModel)),
                    () -> {
                        Integer parentId = Integer.valueOf(parentEntityModel.getValue("id").getValue().toString());
                        Entity parentEntityType = Entity.getEntityType(parentEntityModel);
                        openDetailTab(parentId, parentEntityType);
                    });
        }

        if (entityType == Entity.GHERKIN_TEST) {
            addMenuItem(
                    menu,
                    "Download script",
                    null, // TODO no icon
                    () -> {
                        System.out.println("Please imlement me");
                    });
        }

        if (entityType == Entity.DEFECT ||
                entityType == Entity.USER_STORY ||
                entityType == Entity.QUALITY_STORY ||
                entityType == Entity.TASK) {

            addMenuItem(
                    menu,
                    "Stop work",
                    ImageResources.STOP_TIMER_16X16.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    });

            addMenuItem(
                    menu,
                    "Start work",
                    ImageResources.START_TIMER_16X16.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    });
        }

        if (myWorkService.isAddingToMyWorkSupported(entityType) && MyWorkUtil.isUserItemDismissible(userItem)) {
            addMenuItem(
                    menu,
                    "Dismiss",
                    ImageResources.DISMISS.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    });
        }

        return menu;
    }

    private void addMenuItem(Menu menu, String text, Image image, Runnable selectAction) {
        MenuItem menuItem = new MenuItem(menu, SWT.NONE);
        if (image != null) {
            menuItem.setImage(image);
        }
        menuItem.setText(text);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAction.run();
            }
        });
    }

}
