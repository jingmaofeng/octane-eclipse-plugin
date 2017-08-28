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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.ideplugins.services.CommentService;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.exception.ServiceException;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.ui.FormField;
import com.hpe.adm.octane.ideplugins.services.ui.FormLayout;
import com.hpe.adm.octane.ideplugins.services.ui.FormLayoutSection;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class GetEntityDetailsJob extends Job {

    private static final List<Entity> noPhaseEntites = Arrays.asList(Entity.MANUAL_TEST_RUN, Entity.TEST_SUITE_RUN, Entity.TEST_SUITE);
    private static final List<Entity> noCommentsEntites = Arrays.asList(Entity.TASK, Entity.MANUAL_TEST_RUN, Entity.TEST_SUITE_RUN);

    private long entityId;
    private boolean shouldShowPhase = false;
    private boolean areCommentsLoaded = false;
    private boolean areCommentsShown = false;
    private boolean wasEntityRetrived = false;
    private Entity entityType;
    private EntityModel retrivedEntity;
    private FieldModel currentPhase;
    private FormLayout octaneEntityForm;
    private Collection<EntityModel> possibleTransitions;
    private Collection<EntityModel> comments;

    private MetadataService metadataService = Activator.getInstance(MetadataService.class);
    private EntityService entityService = Activator.getInstance(EntityService.class);
    private CommentService commentService = Activator.getInstance(CommentService.class);

    public GetEntityDetailsJob(String name, Entity entityType, long entityId) {
        super(name);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try {
            
            octaneEntityForm = metadataService.getFormLayoutForSpecificEntityType(this.entityType);
            
            Set<String> fields = new HashSet<String>();
            
            List<FormField> formFields = octaneEntityForm.getFormLayoutSections().stream().collect(Collectors.toList()).get(0).getFields();
            for(FormField formField: formFields) {
            	fields.add(formField.getName());
            }
            
            retrivedEntity = entityService.findEntity(this.entityType, this.entityId, fields);
            getPhaseAndPossibleTransitions();
            getComments();
            wasEntityRetrived = true;
        } catch (ServiceException | UnsupportedEncodingException e) {
            wasEntityRetrived = false;
            e.printStackTrace();
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

    private void getComments() {
        if (noCommentsEntites.contains(Entity.getEntityType(retrivedEntity))) {
            areCommentsShown = false;
        } else {
            areCommentsShown = true;
            comments = commentService.getComments(retrivedEntity);
            areCommentsLoaded = true;
        }
    }

    public String getCommentsForCurrentEntity() {
        StringBuilder commentsBuilder = new StringBuilder();

        commentsBuilder.append("<html><body>");
        if (!comments.isEmpty()) {
            for (EntityModel comment : comments) {
                String commentsPostTime = Util.getUiDataFromModel(comment.getValue(EntityFieldsConstants.FIELD_CREATION_TIME));
                String userName = Util.getUiDataFromModel(comment.getValue(EntityFieldsConstants.FIELD_AUTHOR), "full_name");
                String commentLine = Util.getUiDataFromModel(comment.getValue(EntityFieldsConstants.FIELD_COMMENT_TEXT));
                commentLine = removeHtmlBaseTags(commentLine);
                String currentText = commentsPostTime + " <b>" + userName + ":</b> <br>" + commentLine + "<hr>";
                commentsBuilder.append(currentText);
            }
        }
        commentsBuilder.append("</body></html>");
        return commentsBuilder.toString();
    }

    private static String removeHtmlBaseTags(String htmlString) {
        htmlString = htmlString.replace("<html>", "");
        htmlString = htmlString.replace("<body>", "");
        htmlString = htmlString.replace("</html>", "");
        htmlString = htmlString.replace("</body>", "");
        return htmlString;
    }

    public boolean areCommentsLoaded() {
        return areCommentsLoaded;
    }

    public boolean shouldCommentsBeShown() {
        return areCommentsShown;
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

    public boolean shouldShowPhase() {
        return shouldShowPhase;
    }

}
