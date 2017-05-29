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
        System.out.println(" >> providing commit message");
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