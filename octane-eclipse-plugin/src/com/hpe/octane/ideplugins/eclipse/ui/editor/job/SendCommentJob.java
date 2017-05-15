package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.CommentService;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class SendCommentJob extends Job {
    private EntityModel commentParentEntity;
    private String commentText;
    private CommentService commentService = Activator.getInstance(CommentService.class);
    private boolean isCommentSaved = false;

    public SendCommentJob(String name, EntityModel entityModel, String commentText) {
        super(name);
        this.commentParentEntity = entityModel;
        this.commentText = commentText;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try {
            commentService.postComment(commentParentEntity, commentText);
            isCommentSaved = true;
        } catch (Exception e) {
            isCommentSaved = false;
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    public boolean isCommentsSaved() {
        return isCommentSaved;
    }
}
