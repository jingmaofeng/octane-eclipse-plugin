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
import org.eclipse.egit.ui.internal.staging.StagingView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

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

    public static IPartListener stagingViewListener = new IPartListener() {

        @Override
        public void partOpened(IWorkbenchPart part) {
            setCommitMessage(part);
        }

        @Override
        public void partDeactivated(IWorkbenchPart part) {
            // setCommitMessage(part);
        }

        @Override
        public void partClosed(IWorkbenchPart part) {
            // setCommitMessage(part);
        }

        @Override
        public void partBroughtToTop(IWorkbenchPart part) {
            setCommitMessage(part);
        }

        @Override
        public void partActivated(IWorkbenchPart part) {
            setCommitMessage(part);
        }

        private void setCommitMessage(IWorkbenchPart part) {
            if (part instanceof StagingView && Activator.getActiveItem() != null) {
                System.out.println(" >> brought to top staging view");
                changeMessageIfValid((StagingView) part);
            }
        }
    };

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

    public static void changeMessageIfValid(StagingView stagingView) {

        Display display = Display.getCurrent();
        new Job("Validating commit message ...") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Validating commit message ...", IProgressMonitor.UNKNOWN);

                final boolean valid = validate();
                monitor.done();
                display.asyncExec(() -> {
                    System.out.println(" >> valid = " + valid);
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    if (page.findView(StagingView.VIEW_ID) != null) {
                        if (valid) {
                            String message = getCommitMessageForActiveItem();
                            System.out.println(" >> message = " + message);
                            stagingView.setCommitMessage(message);
                        } else {
                            stagingView.setCommitMessage("");
                            String patterns = Activator.getInstance(CommitMessageService.class).getCommitPatternsForStoryType(
                                    Activator.getActiveItem().getEntityType()).stream().collect(Collectors.joining(", "));
                            new InfoPopup("Commit message", "Please make sure your commit message " +
                                    "matches one of the following patterns: " +
                                    patterns, 400, 70).open();
                        }
                        // stagingView.resetCommitMessageComponent();
                        stagingView.refreshViewersPreservingExpandedElements();
                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();

        // TimerTask task = new TimerTask() {
        //
        // @Override
        // public void run() {
        // Display display2 = Display.getCurrent();
        // System.out.println(" >> 2.display = " + display2);
        //
        // display.asyncExec(() -> {
        // stagingView.setCommitMessage("loading commit message ...");
        //
        // BusyIndicator.showWhile(Display.getCurrent(), () -> {
        // boolean valid = validate();
        // System.out.println(" >> valid = " + valid);
        // if (valid) {
        // String message = getCommitMessageForActiveItem();
        // System.out.println(" >> message = " + message);
        // stagingView.setCommitMessage(message);
        // } else {
        // // TODO
        // stagingView.setCommitMessage("<<commit message is invalid>>");
        // }
        // stagingView.resetCommitMessageComponent();
        // stagingView.refreshViewersPreservingExpandedElements();
        // });
        // });
        // }
        // };
        // new Timer().schedule(task, 200);
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
                System.out.println(" >> parentId = " + parentStory.getValue("id").getValue());
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
