package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class ChangePhaseJob extends Job {
    private EntityModel selectedPhase;
    private EntityModel openedEntity;
    private EntityService entityService = Activator.getInstance(EntityService.class);
    private boolean wasChanged = false;
    private String errorMessage;

    public ChangePhaseJob(String name, EntityModel openedEntity, EntityModel selectedPhase) {
        super(name);
        this.selectedPhase = selectedPhase;
        this.openedEntity = openedEntity;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try {
            entityService.updateEntityPhase(openedEntity, (ReferenceFieldModel) selectedPhase.getValue("target_phase"));
            wasChanged = true;
        } catch (Exception e) {
            wasChanged = false;
            errorMessage = e.getMessage();
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    public boolean isPhaseChanged() {
        return wasChanged;
    }

    public String getFailedReason() {
        return errorMessage;
    }

}
