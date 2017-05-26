package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.CommentService;
import com.hpe.adm.octane.services.exception.ServiceRuntimeException;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class GetCommentsJob extends Job {
    private CommentService commentService = Activator.getInstance(CommentService.class);
    private Collection<EntityModel> comments;
    private EntityModel parentEntiy;
    private boolean areCommentsLoaded = false;

    public GetCommentsJob(String name, EntityModel parentEntiy) {
        super(name);
        this.parentEntiy = parentEntiy;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try {
            comments = commentService.getComments(parentEntiy);
            areCommentsLoaded = true;
        } catch (ServiceRuntimeException e) {
            areCommentsLoaded = false;
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    public Collection<EntityModel> getCoomentsForCurrentEntity() {
        if (comments.isEmpty()) {
            comments.add(new EntityModel("target_phase", "No comments"));
        }
        return comments;
    }

    public boolean areCommentsLoaded() {
        return areCommentsLoaded;
    }
}
