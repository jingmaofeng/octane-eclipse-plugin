package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;

/**
 * This class is used to create a menu on the fly for any control inside the an
 * EntityModelRow (Or any composite designed to show an EntityModel). <br>
 * The context menu is created on the fly using the provided factory whenever a
 * menuDetected event happens on any item of the entityModelRow
 *
 */
class EntityModelRowMenuDetectListener implements MenuDetectListener {

    private EntityModel entityModel;
    private EntityModelMenuFactory menuFactory;

    public EntityModelRowMenuDetectListener(Composite entityModelComposite, EntityModel entityModel, EntityModelMenuFactory menuFactory) {
        this.entityModel = entityModel;
        this.menuFactory = menuFactory;
        addMenuDetectListener(entityModelComposite, this);
    }

    private static void addMenuDetectListener(Control control, EntityModelRowMenuDetectListener listener) {
        if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
                addMenuDetectListener(child, listener);
            }
        }
        control.addMenuDetectListener(listener);
    }

    @Override
    public void menuDetected(MenuDetectEvent e) {
        if (e.widget instanceof Control) {
            Control control = (Control) e.widget;

            // Dispose menu contents
            if (control.getMenu() != null && !control.getMenu().isDisposed()) {
                control.getMenu().dispose();
            }

            Menu menu = menuFactory.createMenu(entityModel, control);
            control.setMenu(menu);

            menu.addMenuListener(new MenuListener() {
                @Override
                public void menuShown(MenuEvent e) {
                }

                @Override
                public void menuHidden(MenuEvent e) {
                    menu.dispose();
                    control.setMenu(null);
                }
            });
        }
    }
}