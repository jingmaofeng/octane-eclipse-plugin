package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;

public class CallEditor extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // get the page
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();
        // get the selection
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection != null && selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();

            // if we had a selection lets open the editor
            if (obj != null) {
                EntityModel entityModel = (EntityModel) obj;
                Long id = Long.parseLong(entityModel.getValue("id").getValue().toString());

                EntityModelEditorInput entityModelEditorInput = new EntityModelEditorInput(id, Entity.getEntityType(entityModel));
                try {
                    page.openEditor(entityModelEditorInput, EntityModelEditor.ID);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
