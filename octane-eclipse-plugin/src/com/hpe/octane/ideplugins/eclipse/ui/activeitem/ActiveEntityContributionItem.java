package com.hpe.octane.ideplugins.eclipse.ui.activeitem;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class ActiveEntityContributionItem extends WorkbenchWindowControlContribution {

    private static final ILog logger = Activator.getDefault().getLog();
    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 8);
    private static ToolBarManager manager;
    private static EntityModelEditorInput entityModelEditorInput;
    private static ToolBar toolbar;
    private static Action openAction = new Action() {
        @Override
        public void run() {
            IWorkbench wb = PlatformUI.getWorkbench();
            IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
            IWorkbenchPage page = win.getActivePage();
            try {
                page.openEditor(entityModelEditorInput, EntityModelEditor.ID);
            } catch (PartInitException ex) {
            }
        }
    };

    public ActiveEntityContributionItem() {
        Activator.addActiveItemChangedHandler(() -> {
            entityModelEditorInput = Activator.getActiveItem();
            addAction();
        });
        entityModelEditorInput = Activator.getActiveItem();
    }

    public ActiveEntityContributionItem(String id) {
        super(id);
        Activator.addActiveItemChangedHandler(() -> {
            entityModelEditorInput = Activator.getActiveItem();
            addAction();
        });
        entityModelEditorInput = Activator.getActiveItem();
    }

    @Override
    protected Control createControl(Composite parent) {
        try {
            // Brilliant
            toolbar = (ToolBar) parent.getParent();
            manager = (ToolBarManager) toolbar.getData();
            addAction();
        } catch (Exception e) {
            logger.log(new Status(
                    Status.ERROR,
                    Activator.PLUGIN_ID,
                    Status.OK,
                    "Failed to add active item menu contribution to toolbar",
                    e));
        }
        return null;
    }

    private static void addAction() {
        manager.removeAll();

        if (entityModelEditorInput != null) {
            openAction.setText(entityModelEditorInput.getId() + "");
            Image img = entityIconFactory.getImageIcon(entityModelEditorInput.getEntityType());
            openAction.setImageDescriptor(
                    new ImageDataImageDescriptor(img.getImageData()));
            openAction.setEnabled(true);
        } else {
            openAction.setImageDescriptor(
                    new ImageDataImageDescriptor(ImageResources.DISMISS.getImage().getImageData()));
            openAction.setText("No active item");
            openAction.setEnabled(false);
        }

        ActionContributionItem contributionItem = new ActionContributionItem(openAction);
        contributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
        manager.add(contributionItem);
        manager.update(true);

        // Just perfect
        toolbar.getParent().getParent().layout(true, true);
        toolbar.getParent().redraw();
        toolbar.getParent().update();

    }

}
