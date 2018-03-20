package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.LinkInterceptListener;
import com.hpe.octane.ideplugins.eclipse.ui.util.PropagateScrollBrowserFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.StackLayoutComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class DescriptionComposite extends Composite{
    
    private PropagateScrollBrowserFactory factory = new PropagateScrollBrowserFactory();
    private Color foregroundColor = PlatformResourcesManager.getPlatformForegroundColor();
    private Color backgroundColor = PlatformResourcesManager.getPlatformBackgroundColor();
    private EntityModel entityModel;
    private Text txtDescHtml;
    private Browser browserDescHtml;
    private boolean fml;

    public DescriptionComposite(Composite parent, int style) {
        super(parent, style);
        
        setLayout(new FillLayout(SWT.HORIZONTAL));
        StackLayoutComposite stackLayoutComposite = new StackLayoutComposite(this, SWT.NONE);
        
        txtDescHtml = new Text(stackLayoutComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

        browserDescHtml = factory.createBrowser(stackLayoutComposite, SWT.NONE);
        browserDescHtml.addLocationListener(new LinkInterceptListener());
        stackLayoutComposite.showControl(browserDescHtml);
        
        txtDescHtml.addModifyListener(e -> {
            StringFieldModel descriptionFieldModel = new StringFieldModel("description", txtDescHtml.getText());
            entityModel.setValue(descriptionFieldModel);
            browserDescHtml.setText(getBrowserText(entityModel));
        });
        
        fml = false;
        
        //Switch
        browserDescHtml.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                stackLayoutComposite.showControl(txtDescHtml);
                fml = false;
            }
        });
       
        txtDescHtml.addListener(SWT.FocusOut, new Listener() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("focus out");
                if(fml) {
                    System.out.println("pass");
                    stackLayoutComposite.showControl(browserDescHtml);
                } else {
                    fml = true;
                }
            }
        });
       
    }
    
    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        txtDescHtml.setText(Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_DESCRIPTION)));
        browserDescHtml.setText(getBrowserText(entityModel));
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
