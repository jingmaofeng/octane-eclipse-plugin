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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.nonentity.ImageService;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
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
        String descriptionFromServerRemodeled = Activator.getInstance(ImageService.class)
                .downloadPictures(Util.getUiDataFromModel(entityModel.getValue((EntityFieldsConstants.FIELD_DESCRIPTION))));

        StringBuilder descriptionText = new StringBuilder();
        descriptionText.append("<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color =" + getRgbString(foregroundColor) + ">");
        descriptionText.append(descriptionFromServerRemodeled);
        descriptionText.append("</font></body></html>");

        String verifyIfEmpty = "<html>\n <head></head>\n <body></body>\n</html>";
        if (verifyIfEmpty.equals(descriptionFromServerRemodeled)) {
            return "<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color =" + getRgbString(foregroundColor) + ">"
                    + "No description" + "</font></body></html>";
        } else {
            return descriptionText.toString();
        }
    }

    private static String getRgbString(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

}
