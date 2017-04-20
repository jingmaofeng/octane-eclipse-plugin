package com.hpe.octane.ideplugins.eclipse.ui.mywork.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.mywork.MyWorkService;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class DismissItemJob extends Job {

    MyWorkService myWorkService = Activator.getInstance(MyWorkService.class);
    EntityModel entityModel;
    private boolean wasRemoved = false;

    public DismissItemJob(String name, EntityModel entityModel) {
        super(name);
        this.entityModel = entityModel;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        wasRemoved = myWorkService.removeFromMyWork(entityModel);
        monitor.done();
        return Status.OK_STATUS;
    }

    public boolean wasRemoved() {
        return wasRemoved;
    }

}
