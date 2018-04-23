/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.field;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.IOUtils;
import com.hpe.adm.nga.sdk.authentication.SimpleUserAuthentication;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.ClientLoginCookie;
import com.hpe.octane.ideplugins.eclipse.ui.util.LinkInterceptListener;
import com.hpe.octane.ideplugins.eclipse.ui.util.PropagateScrollBrowserFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class DescriptionComposite extends Composite {

    private PropagateScrollBrowserFactory factory = new PropagateScrollBrowserFactory();
    private Color foregroundColor = PlatformResourcesManager.getPlatformForegroundColor();
    private Color backgroundColor = PlatformResourcesManager.getPlatformBackgroundColor();
    private Browser browserDescHtml;
    private File octanePhotosDir;

    public DescriptionComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout(SWT.HORIZONTAL));
        StackLayoutComposite stackLayoutComposite = new StackLayoutComposite(this, SWT.NONE);
        browserDescHtml = factory.createBrowser(stackLayoutComposite, SWT.NONE);
        browserDescHtml.addLocationListener(new LinkInterceptListener());
        stackLayoutComposite.showControl(browserDescHtml);
    }

    public void setEntityModel(EntityModelWrapper entityModelWrapper) {
        browserDescHtml.setText(getBrowserText(entityModelWrapper.getReadOnlyEntityModel()));
    }

    public String getBrowserText(EntityModel entityModel) {
        String descriptionFromServerRemodeled = Util.getUiDataFromModel(entityModel.getValue((EntityFieldsConstants.FIELD_DESCRIPTION)));
        try {
            descriptionFromServerRemodeled = downloadPictures(
                    Util.getUiDataFromModel(entityModel.getValue((EntityFieldsConstants.FIELD_DESCRIPTION))));
        } catch (IOException e) {
        }

        String descriptionText = "<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
                + getRgbString(foregroundColor) + ">"
                + descriptionFromServerRemodeled
                + "</font></body></html>";
        if (descriptionText.equals("<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
                + getRgbString(foregroundColor) + ">" + "</font></body></html>")) {

            return "<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
                    + getRgbString(foregroundColor) + ">" + "No description" + "</font></body></html>";
        } else {
            return descriptionText;
        }

    }

    private static String getRgbString(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }
    
    private String downloadPictures(String descriptionField) throws IOException {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String baseUrl = Activator.getConnectionSettings().getBaseUrl();
        File tmpDir = new File(tmpPath);
        octanePhotosDir = new File(tmpDir, "Octane_pictures");

        if (!new File(tmpDir, "Octane_pictures").exists()) {
            octanePhotosDir.mkdir();
        }
        String octanePhotosPath = octanePhotosDir.getAbsolutePath();

        Document descriptionParser = Jsoup.parse(descriptionField);
        Elements link = descriptionParser.getElementsByTag("img");
        
        for (Element el : link) {
            String pictureLink = el.attr("src");
            if (pictureLink.contains("/api/shared_spaces")) {
                el.attr("src", baseUrl + pictureLink);
                pictureLink = el.attr("src");
            }

            if (!pictureLink.contains(Activator.getConnectionSettings().getBaseUrl())) {
                continue;
            }
            int index = pictureLink.lastIndexOf("/");
            String pictureName = pictureLink.substring(index + 1, pictureLink.length());
            String picturePath = octanePhotosPath + "\\" + pictureName;
            if (!new File(octanePhotosDir, pictureName).exists()) {
                saveImage(pictureLink, picturePath);
            }
            ;
            el.attr("src", picturePath);
        }
        return descriptionParser.toString();
    }

    private void saveImage(String pictureLink, String octanePhotosName) throws IOException {

        if (isUserLoggedIn() == true) {
            HttpResponse httpResponse = ClientLoginCookie.getDataForImage(pictureLink);
            InputStream is = httpResponse.getContent();

            OutputStream os = new FileOutputStream(octanePhotosName);
            IOUtils.copy(is, os);
            is.close();
            os.close();
        } else {
            loginUser();
        }
    }

    private boolean isUserLoggedIn() {
        if (ClientLoginCookie.isUserLoggedIn() == false) {
            loginUser();
            return true;
        } else {
            return true;
        }
    }

    private void loginUser() {
        SimpleUserAuthentication userAuth = new SimpleUserAuthentication(Activator.getConnectionSettings().getUserName(),
                Activator.getConnectionSettings().getPassword());
        HttpResponse httpResponse = ClientLoginCookie.loginClient(userAuth);
    }

}
