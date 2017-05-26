package com.hpe.octane.ideplugins.eclipse.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.egit.ui.ICommitMessageProvider;

import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.CommitMessageUtil;

public class OctaneCommitMessageProvider implements ICommitMessageProvider {

    @Override
    public String getMessage(IResource[] resources) {
        System.out.println(" >> providing commit message");
        if (Activator.getActiveItem() != null) {
            return CommitMessageUtil.getCommitMessageForActiveItem();
        } else {
            return null;
        }
    }

}