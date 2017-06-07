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
package com.hpe.octane.ideplugins.eclipse;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.query.Query;
import com.hpe.adm.nga.sdk.query.QueryMethod;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.nonentity.CommitMessageService;
import com.hpe.octane.ideplugins.eclipse.ui.editor.EntityModelEditorInput;
import com.hpe.octane.ideplugins.eclipse.util.InfoPopup;

public class CommitMessageUtil {

    private static EntityModel parentStory;

    public static String getCommitMessageForActiveItem() {
        EntityModelEditorInput activeItem = Activator.getActiveItem();
        if (activeItem == null) {
            return null;
        } else {
            StringBuilder messageBuilder = new StringBuilder();
            if (activeItem.getEntityType() == Entity.TASK) {
                messageBuilder.append(Entity.getEntityType(parentStory).toString().toLowerCase().replace("_", " "));
                messageBuilder.append(" #" + parentStory.getValue("id").getValue() + ": ");
                messageBuilder.append(parentStory.getValue("name").getValue() + "\n");
            }

            messageBuilder.append(activeItem.getEntityType().toString().toLowerCase().replace("_", " "));
            messageBuilder.append(" #" + activeItem.getId() + ": ");
            messageBuilder.append(activeItem.getTitle());

            return messageBuilder.toString();
        }
    }

    public static void copyMessageIfValid() {
        Display display = Display.getCurrent();
        new Job("Validating commit message ...") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Validating commit message ...", IProgressMonitor.UNKNOWN);

                final boolean valid = validate();
                monitor.done();
                display.asyncExec(() -> {
                    if (valid) {
                        Clipboard cp = new Clipboard(display);
                        TextTransfer textTransfer = TextTransfer.getInstance();
                        cp.setContents(new Object[] { getCommitMessageForActiveItem() }, new Transfer[] { textTransfer });
                    } else {
                        new Job("Getting commit patterns ...") {

                            @Override
                            protected IStatus run(IProgressMonitor monitor) {
                                monitor.beginTask("Getting commit patterns ...", IProgressMonitor.UNKNOWN);

                                String patterns = Activator.getInstance(CommitMessageService.class).getCommitPatternsForStoryType(
                                        Activator.getActiveItem().getEntityType()).stream().collect(Collectors.joining(", "));
                                monitor.done();
                                display.asyncExec(() -> {
                                    new InfoPopup("Commit message", "Please make sure your commit message " +
                                            "matches one of the following patterns: " +
                                            patterns, 400, 70).open();
                                });

                                return Status.OK_STATUS;
                            }
                        }.schedule();
                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    public static boolean validate() {
        EntityModelEditorInput activeItem = Activator.getActiveItem();
        if (activeItem.getEntityType() == Entity.TASK) {
            Set<String> storyField = new HashSet<>(Arrays.asList("story"));
            Query.QueryBuilder idQuery = Query.statement("id", QueryMethod.EqualTo, activeItem.getId());
            EntityService entityService = Activator.getInstance(EntityService.class);
            Collection<EntityModel> results = entityService.findEntities(Entity.TASK, idQuery, storyField);
            if (!results.isEmpty()) {
                parentStory = (EntityModel) results.iterator().next().getValue("story").getValue();
            }
        }

        CommitMessageService commitService = Activator.getInstance(CommitMessageService.class);
        String commitMessage = getCommitMessageForActiveItem();
        if (activeItem.getEntityType() == Entity.TASK) {
            return commitService.validateCommitMessage(commitMessage, Entity.getEntityType(parentStory),
                    Long.parseLong(parentStory.getValue("id").getValue().toString()));
        } else {
            return commitService.validateCommitMessage(commitMessage, activeItem.getEntityType(),
                    activeItem.getId());
        }
    }

}
