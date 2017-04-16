package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import static com.hpe.adm.octane.services.util.Util.getUiDataFromModel;

import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.adm.octane.services.util.UrlParser;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;
import com.hpe.octane.ideplugins.eclipse.util.DebugUtil;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class MyWorkEntityModelMenuFactory implements EntityModelMenuFactory {

    // private static final ILog logger = Activator.getDefault().getLog();

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(16, 16, 7);
    private static EntityService entityService = DebugUtil.serviceModule.getInstance(EntityService.class);
    private static MyWorkService myWorkService = DebugUtil.serviceModule.getInstance(MyWorkService.class);
    private ViewPart parentViewPart;
    private EntityListData entityListData;

    public MyWorkEntityModelMenuFactory(ViewPart parentViewPart, EntityListData entityListData) {
        this.parentViewPart = parentViewPart;
        this.entityListData = entityListData;
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
                "View in browser (System)",
                ImageResources.BROWSER_16X16.getImage(),
                () -> entityService.openInBrowser(entityModel));

        addMenuItem(
                menu,
                "View in browser (Eclipse)",
                ImageResources.BROWSER_16X16.getImage(),
                () -> {
                    Entity ownerEntityType = null;
                    Integer ownerEntityId = null;
                    if (entityType == Entity.COMMENT) {
                        ReferenceFieldModel owner = (ReferenceFieldModel) Util.getContainerItemForCommentModel(entityModel);
                        ownerEntityType = Entity.getEntityType(owner.getValue());
                        ownerEntityId = Integer.valueOf(Util.getUiDataFromModel(owner, "id"));
                    }
                    URI uri = UrlParser.createEntityWebURI(
                            Activator.getConnectionSettings(),
                            entityType == Entity.COMMENT ? ownerEntityType : entityType,
                            entityType == Entity.COMMENT ? ownerEntityId : entityId);
                    try {
                        PlatformUI.getWorkbench().getBrowserSupport().createBrowser(uri.toString()).openURL((uri.toURL()));
                    } catch (PartInitException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

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
                    ImageResources.DOWNLOAD.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    });
        }

        if (entityType == Entity.DEFECT ||
                entityType == Entity.USER_STORY ||
                entityType == Entity.QUALITY_STORY ||
                entityType == Entity.TASK) {

            new MenuItem(menu, SWT.SEPARATOR);

            addMenuItem(
                    menu,
                    "Start work",
                    ImageResources.START_TIMER_16X16.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    });

            addMenuItem(
                    menu,
                    "Stop work",
                    ImageResources.STOP_TIMER_16X16.getImage(),
                    () -> {
                        System.out.println("Please imlement me");
                    }).setEnabled(false);
        }

        if (myWorkService.isAddingToMyWorkSupported(entityType) && MyWorkUtil.isUserItemDismissible(userItem)) {
            new MenuItem(menu, SWT.SEPARATOR);
            addMenuItem(
                    menu,
                    "Dismiss",
                    ImageResources.DISMISS.getImage(),
                    () -> {
                        // lambdaception
                        menuParent.getDisplay().asyncExec(() -> {
                            boolean removed = myWorkService.removeFromMyWork(entityModel);
                            if (removed) {
                                entityListData.remove(userItem);
                            }
                        });
                    });
        }

        return menu;
    }

    private MenuItem addMenuItem(Menu menu, String text, Image image, Runnable selectAction) {
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
        return menuItem;
    }

}
