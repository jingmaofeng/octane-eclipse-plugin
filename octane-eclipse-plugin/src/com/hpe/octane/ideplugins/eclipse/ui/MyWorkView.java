package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.TemporaryDataProvider;

public class MyWorkView extends ViewPart {
	
    public static final String ID = "com.vogella.rcp.editor.example.taskoverview";

    private ListViewer         viewer;

    @Override
    public void createPartControl(Composite parent) {
        viewer = new ListViewer(parent);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                EntityModel p = (EntityModel) element;
                return p.getValue("name").getValue().toString();
            };
        });
        viewer.setInput(TemporaryDataProvider.findEntities(Entity.USER_STORY));
        getSite().setSelectionProvider(viewer);
        hookDoubleClickCommand();
    }

    private void hookDoubleClickCommand() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IHandlerService handlerService = getSite().getService(IHandlerService.class);
                try {
                	
                    Object obj = viewer.getStructuredSelection().getFirstElement();
                    if(obj instanceof EntityModel){
                    	//ContributionItem1.setLblText(((EntityModel) obj).getValue("name").getValue().toString());
                    }                
                	
                    handlerService.executeCommand("octane-eclipse-plugin.openEntityEditor", null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        });
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
}
