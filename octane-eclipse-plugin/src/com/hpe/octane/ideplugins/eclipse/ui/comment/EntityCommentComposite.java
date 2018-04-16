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
package com.hpe.octane.ideplugins.eclipse.ui.comment;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.lang.SystemUtils;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.GetCommentsJob;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.PostCommentJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.LoadingComposite.LoadingPosition;
import com.hpe.octane.ideplugins.eclipse.ui.util.PropagateScrollBrowserFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class EntityCommentComposite extends StackLayoutComposite {

    private EntityModel entityModel;

    private Browser commentsBrowser;
    private LoadingComposite loadingComposite;
    private Composite commentsComposite;

    private Text commentText;

    private Button postCommentBtn;
    private Label separator;

    public EntityCommentComposite(Composite parent, int style) {
        super(parent, style);

        loadingComposite = new LoadingComposite(this, SWT.NONE);
        loadingComposite.setLoadingVerticalPosition(LoadingPosition.TOP);

        commentsComposite = new Composite(this, SWT.NONE);
        GridLayout gl_commentsComposite = new GridLayout(2, false);
        gl_commentsComposite.marginWidth = 0;
        gl_commentsComposite.horizontalSpacing = 10;
        commentsComposite.setLayout(gl_commentsComposite);

        separator = new Label(commentsComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData sepGridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
        sepGridData.widthHint = 2;
        separator.setLayoutData(sepGridData);

        Label commentsTitleLabel = new Label(commentsComposite, SWT.NONE);
        commentsTitleLabel.setText("Comments");
        commentsTitleLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

        Composite inputCommentAndSendButtonComposite = new Composite(commentsComposite, SWT.NONE);
        inputCommentAndSendButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        inputCommentAndSendButtonComposite.setLayout(new GridLayout(2, false));

        commentText = new Text(inputCommentAndSendButtonComposite, SWT.BORDER);
        commentText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        commentText.setToolTipText("Add new comment");
        commentText.addListener(SWT.Traverse, (Event event) -> {
            if (event.detail == SWT.TRAVERSE_RETURN && commentText.isEnabled()) {
                postComment(commentText.getText());
            }
        });

        postCommentBtn = new Button(inputCommentAndSendButtonComposite, SWT.NONE);
        postCommentBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        postCommentBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (commentText.isEnabled()) {
                    postComment(commentText.getText());
                    commentText.setEnabled(false);
                }
            }
        });
        postCommentBtn.setText("Post");

        commentText.setEnabled(false);
        postCommentBtn.setEnabled(false);

        PropagateScrollBrowserFactory browserFactory = new PropagateScrollBrowserFactory();
        commentsBrowser = browserFactory.createBrowser(commentsComposite, SWT.NONE);
        commentsBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        showControl(commentsComposite);
        commentsBrowser.setText("<html></html>");
        commentsBrowser.addLocationListener(new LocationAdapter() {
            // method called when the user clicks a link but before the link is
            // opened
            @Override
            public void changing(LocationEvent event) {
                String urlString = event.location;
                if (urlString == null || "about:blank".equals(urlString)) {
                    return;
                }

                try {
                    URIBuilder url = new URIBuilder(urlString);

                    if (url.getHost() != null) {
                        String temporaryString = url.toString();
                        URI finalUrl = new URI(temporaryString);
                        if (SystemUtils.IS_OS_LINUX) {
                            String finalUrlToString = finalUrl.toString();
                            if (Runtime.getRuntime().exec(new String[] { "which", "xdg-open" }).getInputStream().read() != -1) {
                                Runtime.getRuntime().exec(new String[] { "xdg-open", finalUrlToString });
                            }
                        } else {
                            Desktop.getDesktop().browse(finalUrl);
                        }
                        event.doit = false;
                        return;
                    }

                    URI baseURI = new URI(Activator.getConnectionSettings().getBaseUrl());
                    url.setHost(baseURI.getHost());
                    url.setPort(baseURI.getPort());
                    url.setScheme(baseURI.getScheme());

                    String temporaryString = url.toString();
                    URI finalUrl = new URI(temporaryString);
                    if (SystemUtils.IS_OS_LINUX) {
                        String finalUrlToString = finalUrl.toString();
                        if (Runtime.getRuntime().exec(new String[] { "which", "xdg-open" }).getInputStream().read() != -1) {
                            Runtime.getRuntime().exec(new String[] { "xdg-open", finalUrlToString });
                        }
                    } else {
                        Desktop.getDesktop().browse(finalUrl);
                    }
                    event.doit = false; // stop propagation
                } catch (URISyntaxException | IOException e) {
                    // tough luck, continue propagation, it's better than nothing
                    event.doit = true;
                }
            }
        });
    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        commentText.setEnabled(true);
        postCommentBtn.setEnabled(true);
        displayComments();
    }

    private void postComment(String text) {
        commentText.setEnabled(false);
        showControl(loadingComposite);

        PostCommentJob sendCommentJob = new PostCommentJob("Posting Comment", entityModel, text);
        sendCommentJob.schedule();
        sendCommentJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    if (sendCommentJob.isCommentsSaved()) {
                        displayComments();
                        commentText.setText("");
                        commentText.setEnabled(true);
                    } else {
                        MessageDialog.openError(Display.getCurrent().getActiveShell(), "ERROR",
                                "Comments could not be posted \n ");
                    }
                });
            }
        });
    }

    private void displayComments() {
        GetCommentsJob getCommentsJob = new GetCommentsJob("Getting comments", entityModel);
        getCommentsJob.schedule();
        showControl(loadingComposite);

        getCommentsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(() -> {
                    String html = getCommentHtmlString(getCommentsJob.getComents());
                    if (!commentsBrowser.isDisposed()) {
                        commentsBrowser.setText(html);
                        showControl(commentsComposite);
                    }
                });
            }
        });

    }

    private static String getCommentHtmlString(Collection<EntityModel> comments) {

        Color backgroundColor = PlatformResourcesManager.getPlatformBackgroundColor();
        Color foregroundColor = PlatformResourcesManager.getPlatformForegroundColor();

        String backgroundColorString = "rgb(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() + ")";
        String foregroundColorString = "rgb(" + foregroundColor.getRed() + "," + foregroundColor.getGreen() + "," + foregroundColor.getBlue() + ")";

        StringBuilder commentsBuilder = new StringBuilder();
        commentsBuilder.append("<html><body bgcolor =" + backgroundColorString + ">");
        commentsBuilder.append("<font color =" + foregroundColorString + ">");

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

}
