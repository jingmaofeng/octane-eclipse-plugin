package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.MetadataService;
import com.hpe.adm.octane.services.exception.ServiceException;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.ui.FormLayout;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class GetEntityDetailsJob extends Job {

    private Entity entityType;
    private long entityId;
    private EntityModel retrivedEntity;
    private boolean wasEntityRetrived = false;
    private FieldModel currentPhase;
    private FormLayout octaneEntityForm;
    private Collection<EntityModel> possibleTransitions;
    private MetadataService metadataService = Activator.getInstance(MetadataService.class);
    private EntityService entityService = Activator.getInstance(EntityService.class);

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
            octaneEntityForm = metadataService.getFormLayoutForSpecificEntityType(Entity.getEntityType(retrivedEntity));
            currentPhase = retrivedEntity.getValue("phase");
            Long currentPhaseId = Long.valueOf(Util.getUiDataFromModel(currentPhase, "id"));
            possibleTransitions = entityService.findPossibleTransitionFromCurrentPhase(Entity.getEntityType(retrivedEntity), currentPhaseId);
            wasEntityRetrived = true;
        } catch (ServiceException | UnsupportedEncodingException e) {
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

    public FieldModel getCurrentPhase() {
        return currentPhase;
    }

    public FormLayout getFormForCurrentEntity() {
        return octaneEntityForm;
    }

    public Collection<EntityModel> getPossibleTransitionsForCurrentEntity() {
        if (possibleTransitions.isEmpty()) {
            possibleTransitions.add(new EntityModel("target_phase", "No transition"));
        }
        return possibleTransitions;
    }

}
