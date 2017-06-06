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
package com.hpe.octane.ideplugins.eclipse.ui;

import java.util.stream.Collectors;

import org.eclipse.core.resources.IResource;
import org.eclipse.egit.ui.ICommitMessageProvider;

import com.hpe.adm.octane.services.nonentity.CommitMessageService;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.CommitMessageUtil;
import com.hpe.octane.ideplugins.eclipse.util.InfoPopup;

public class OctaneCommitMessageProvider implements ICommitMessageProvider {

    @Override
    public String getMessage(IResource[] resources) {
        if (Activator.getActiveItem() != null) {
            if (CommitMessageUtil.validate()) {
                return CommitMessageUtil.getCommitMessageForActiveItem();
            }
            String patterns = Activator.getInstance(CommitMessageService.class).getCommitPatternsForStoryType(
                    Activator.getActiveItem().getEntityType()).stream().collect(Collectors.joining(", "));
            new InfoPopup("Commit message", "Please make sure your commit message " +
                    "matches one of the following patterns: " +
                    patterns, 400, 70).open();
            return null;
        } else {
            return null;
        }
    }

}
