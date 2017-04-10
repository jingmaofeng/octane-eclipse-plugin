package com.hpe.octane.ideplugins.eclipse.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditor;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;

public class CallEditor extends AbstractHandler {

    ILog logger = Activator.getDefault().getLog();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        
        if (selection != null && selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj != null) {
                EntityModel entityModel = (EntityModel) obj;
                
            	if(Entity.USER_ITEM == Entity.getEntityType(entityModel)){
            		entityModel = MyWorkUtil.getEntityModelFromUserItem(entityModel);
            	}
          
                Long id = Long.parseLong(entityModel.getValue("id").getValue().toString());

                EntityModelEditorInput entityModelEditorInput = new EntityModelEditorInput(id, Entity.getEntityType(entityModel));
                try {
                    logger.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK, entityModelEditorInput.toString(), null));
                    page.openEditor(entityModelEditorInput, EntityModelEditor.ID);
                } catch (PartInitException e) {
                    logger.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, "An exception has occured when opening the editor", e));
                }
            }
        }
        return null;
    }
}
