package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityMouseListener;

public class OpenDetailTabEntityMouseListener implements EntityMouseListener {

    private static final ILog logger = Activator.getDefault().getLog();

    @Override
    public void mouseClick(EntityModel entityModel, MouseEvent event) {
        // Open detail tab
        if (event.count == 2) {
            IWorkbench wb = PlatformUI.getWorkbench();
            IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
            IWorkbenchPage page = win.getActivePage();

            if (Entity.USER_ITEM == Entity.getEntityType(entityModel)) {
                entityModel = MyWorkUtil.getEntityModelFromUserItem(entityModel);
            }

            if (Entity.COMMENT == Entity.getEntityType(entityModel)) {
                entityModel = (EntityModel) Util.getContainerItemForCommentModel(entityModel).getValue();
            }

            Long id = Long.parseLong(entityModel.getValue("id").getValue().toString());
            EntityModelEditorInput entityModelEditorInput = new EntityModelEditorInput(id, Entity.getEntityType(entityModel));
            try {
                logger.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK, entityModelEditorInput.toString(), null));
                page.openEditor(entityModelEditorInput, EntityModelEditor.ID);
            } catch (PartInitException ex) {
                logger.log(
                        new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, "An exception has occured when opening the editor", ex));
            }
        }
    }

}
