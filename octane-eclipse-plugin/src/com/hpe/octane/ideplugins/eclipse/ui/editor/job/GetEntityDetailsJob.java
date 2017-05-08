package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class GetEntityDetailsJob extends Job {

    private Entity entityType;
    private long entityId;
    private EntityModel retrivedEntity;
    private EntityService entityService = Activator.getInstance(EntityService.class);
    private boolean wasEntityRetrived = false;

    public GetEntityDetailsJob(String name, Entity entityType, long entityId) {
        super(name);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try {
            retrivedEntity = entityService.findEntity(this.entityType, this.entityId);
            wasEntityRetrived = true;
        } catch (ServiceException e) {
            wasEntityRetrived = false;
            e.printStackTrace();
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    public boolean wasEntityRetrived() {
        return wasEntityRetrived;
    }

    public EntityModel getEntiyData() {
        return retrivedEntity;
    }

}
