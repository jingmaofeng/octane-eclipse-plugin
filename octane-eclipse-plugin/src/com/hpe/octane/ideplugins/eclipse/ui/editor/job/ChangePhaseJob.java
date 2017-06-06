/*******************************************************************************
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.exception.OctaneException;
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
        } catch (OctaneException ex) {
            wasChanged = false;
            errorMessage = ex.getMessage();
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
