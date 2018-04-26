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
import com.hpe.adm.octane.ideplugins.services.util.Util;
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

    private String getBrowserText(EntityModel entityModel) {
        String descriptionText = Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION));

        if (descriptionText.isEmpty()) {
            descriptionText = "No description";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<html>");
        sb.append("<html><body style=\"background-color:" + getRgbString(backgroundColor) + ";\">");
        sb.append("<font style=\"color:" + getRgbString(foregroundColor) + "\">");
        sb.append(descriptionText);
        sb.append("</font>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private static String getRgbString(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

}
