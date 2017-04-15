package com.hpe.octane.ideplugins.eclipse.ui.mywork;

import static com.hpe.adm.octane.services.util.Util.getUiDataFromModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;
import com.hpe.octane.ideplugins.eclipse.util.DebugUtil;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class MyWorkEntityModelMenuFactory implements EntityModelMenuFactory {

    // private static EntityService entityService =
    // Activator.getInstance(EntityService.class);
    private static EntityService entityService = DebugUtil.serviceModule.getInstance(EntityService.class);

    @Override
    public Menu createMenu(EntityModel userItem, Control menuParent) {

        Menu menu = new Menu(menuParent);

        EntityModel entityModel = MyWorkUtil.getEntityModelFromUserItem(userItem);
        Entity entityType = Entity.getEntityType(entityModel);
        String entityName = Util.getUiDataFromModel(entityModel.getValue("name"));
        Integer entityId = Integer.valueOf(getUiDataFromModel(entityModel.getValue("id")));

        MenuItem viewInBrowserItem = new MenuItem(menu, SWT.NONE);
        viewInBrowserItem.setImage(ImageResources.BROWSER_16X16.getImage());
        viewInBrowserItem.setText("Menu");
        viewInBrowserItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                entityService.openInBrowser(entityModel);
            }
        });

        return menu;
    }

}
