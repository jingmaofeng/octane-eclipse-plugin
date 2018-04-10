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
        String descriptionText = "<html><body bgcolor =" + getRgbString(backgroundColor) + ">" + "<font color ="
                + getRgbString(foregroundColor) + ">"
                + Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION))
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

}