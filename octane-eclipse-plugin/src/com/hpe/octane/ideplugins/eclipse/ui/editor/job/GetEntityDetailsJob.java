/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.ui.editor.job;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.exception.ServiceException;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class GetEntityDetailsJob extends Job {

    private static final List<Entity> noPhaseEntites = Arrays.asList(Entity.MANUAL_TEST_RUN, Entity.TEST_SUITE_RUN, Entity.TEST_SUITE);

    private long entityId;

    private boolean wasEntityRetrived = false;
    private boolean shouldShowPhase = false;

    private Entity entityType;
    private EntityModel retrivedEntity;

    private FieldModel currentPhase;

    private Collection<EntityModel> possibleTransitions;
    private Collection<FieldMetadata> allEntityFields;

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
            allEntityFields = metadataService.getFields(entityType);
            getPhaseAndPossibleTransitions();
            wasEntityRetrived = true;
        } catch (ServiceException ignored) {
            wasEntityRetrived = false;
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    private void getPhaseAndPossibleTransitions() {
        if (noPhaseEntites.contains(Entity.getEntityType(retrivedEntity))) {
            shouldShowPhase = false;
        } else {
            shouldShowPhase = true;
            currentPhase = retrivedEntity.getValue(EntityFieldsConstants.FIELD_PHASE);
            String currentPhaseId = Util.getUiDataFromModel(currentPhase, EntityFieldsConstants.FIELD_ID);
            possibleTransitions = entityService.findPossibleTransitionFromCurrentPhase(Entity.getEntityType(retrivedEntity), currentPhaseId);
        }
    }

    public boolean shouldShowPhase() {
        return shouldShowPhase;
    }

    public boolean wasEntityRetrived() {
        return wasEntityRetrived;
    }

    public EntityModel getEntiyData() {
        return retrivedEntity;
    }

    public Collection<FieldMetadata> getAllEntityFields() {
        return allEntityFields;
    }

    public FieldModel getCurrentPhase() {
        return currentPhase;
    }

    public Collection<EntityModel> getPossibleTransitionsForCurrentEntity() {
        if (possibleTransitions.isEmpty()) {
            possibleTransitions.add(new EntityModel("target_phase", "No transition"));
        }
        return possibleTransitions;
    }

}