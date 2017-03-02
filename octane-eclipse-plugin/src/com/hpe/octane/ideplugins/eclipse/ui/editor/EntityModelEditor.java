package com.hpe.octane.ideplugins.eclipse.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class EntityModelEditor extends EditorPart {

    public static final String     ID            = "com.hpe.octane.ideplugins.eclipse.ui.EntityModelEditor";             //$NON-NLS-1$

    private EntityModel            entityModel;
    private EntityModelEditorInput input;

    private EntityService          entityService = Activator.getServiceModuleInstance().getInstance(EntityService.class);

    public EntityModelEditor() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        if (!(input instanceof EntityModelEditorInput)) {
            throw new RuntimeException("Wrong input");
        }

        this.input = (EntityModelEditorInput) input;
        setSite(site);
        setInput(input);

        try {
            entityModel = entityService.findEntity(this.input.getEntityType(), this.input.getId());
        } catch (ServiceException e1) {
            e1.printStackTrace();
        }

        setPartName(entityModel.getValue("name").getValue().toString());
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        if (entityModel != null) {
            entityModel.getValues()
                    .stream()
                    .sorted((f1, f2) -> f1.getName().compareTo(f2.getName()))
                    .forEach(fieldModel -> {
                        Label lblNewLabel = new Label(composite, SWT.NONE);
                        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

                        String fieldName = fieldModel.getName();
                        String fieldValue = Util.getUiDataFromModel(fieldModel, "name");
                        lblNewLabel.setText(fieldName + ": " + fieldValue);
                    });
        }

        scrolledComposite.setContent(composite);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void setFocus() {
        // Set the focus
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // Do the Save operation
    }

    @Override
    public void doSaveAs() {
        // Do the Save As operation
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

}