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
package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class EntityModelRow extends Composite {

    public enum DetailsPosition {
        TOP, BOTTOM
    }

    private StyledText lblEntityDetails;
    private Label lblEntityName;
    private Label lblEntityId;
    private Label lblEntityIcon;
    private Composite compositeTopDetails;
    private Composite compositeBottomDetails;
    private Label labelTopSpacer;
    private Label labelBottomSpacer;
    private static StyleRange[] emptyRange;
    private Label lblNewLabel;
    private Composite iconComposite;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public EntityModelRow(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(5, false);
        gridLayout.marginBottom = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        iconComposite = new Composite(this, SWT.NONE);
        FillLayout fl_iconComposite = new FillLayout(SWT.HORIZONTAL);
        fl_iconComposite.marginWidth = 3;
        fl_iconComposite.marginHeight = 3;
        iconComposite.setLayout(fl_iconComposite);
        iconComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));

        lblEntityIcon = new Label(iconComposite, SWT.CENTER);
        lblEntityIcon.setAlignment(SWT.CENTER);

        lblEntityId = new Label(this, SWT.NONE);
        lblEntityId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblEntityId.setFont(SWTResourceManager.getBoldFont(lblEntityId.getFont()));

        lblEntityName = new Label(this, SWT.NONE);
        lblEntityName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

        labelTopSpacer = new Label(this, SWT.NONE);
        labelTopSpacer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

        compositeTopDetails = new Composite(this, SWT.NONE);
        RowLayout rl_compositeTopDetails = new RowLayout(SWT.HORIZONTAL);
        compositeTopDetails.setLayout(rl_compositeTopDetails);
        compositeTopDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));

        lblEntityDetails = new StyledText(this, SWT.NONE);
        lblEntityDetails.setEnabled(false);
        lblEntityDetails.setEditable(false);
        lblEntityDetails.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1));

        labelBottomSpacer = new Label(this, SWT.NONE);
        labelBottomSpacer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

        compositeBottomDetails = new Composite(this, SWT.NONE);
        compositeBottomDetails.setLayout(new RowLayout(SWT.HORIZONTAL));
        compositeBottomDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));

        lblNewLabel = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.CENTER);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
    }

    public void setBackgroundColor(Color color) {
        this.setBackground(color);
    }

    public void setEntityIcon(Image entityIconImage) {
        lblEntityIcon.setImage(entityIconImage);
    }

    public void setEntityName(String entityName) {
        entityName = checkEmptyValue(entityName);
        lblEntityName.setText(entityName);
    }

    public void setEntityId(Integer id) {
        lblEntityId.setText(id + "");
    }

    public void setEntitySubTitle(String subtitle) {
        setEntitySubTitle(subtitle, emptyRange);
    }

    public void setEntitySubTitle(String subtitle, String defaultValue) {
        if (StringUtils.isEmpty(subtitle)) {
            setEntitySubTitle(defaultValue, emptyRange);
        } else {
            setEntitySubTitle(subtitle, emptyRange);
        }
    }

    public void setEntitySubTitle(String subtitle, StyleRange[] styleRanges) {
        try {
            subtitle = checkEmptyValue(subtitle);
            lblEntityDetails.setText(subtitle);
            if (styleRanges != null) {
                lblEntityDetails.setStyleRanges(styleRanges);
            }
        } catch (Exception ex) {

        }
    }

    public void addDetails(String fieldName, String fieldValue, DetailsPosition position) {
        Composite parent;
        if (DetailsPosition.TOP == position) {
            parent = compositeTopDetails;
        } else {
            parent = compositeBottomDetails;
        }

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);

        Label lblSeparator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_label.heightHint = lblSeparator.getFont().getFontData()[0].getHeight() + 2;
        gd_label.verticalIndent = 2;
        lblSeparator.setLayoutData(gd_label);

        if (StringUtils.isNotEmpty(fieldName)) {
            Label lblKey = new Label(composite, SWT.NONE);
            GridData gd_lblKey = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_lblKey.verticalIndent = 2;
            lblKey.setLayoutData(gd_lblKey);
            lblKey.setForeground(SWTResourceManager.getColor(128, 128, 128));
            lblKey.setText(fieldName + ": ");
        }

        Label lblValue = new Label(composite, SWT.NONE);
        lblValue.setForeground(SWTResourceManager.getColor(0, 0, 0));
        Font font = lblValue.getFont();
        lblValue.setFont(SWTResourceManager.getBiggerFont(font, font.getFontData()[0].getHeight() + 1));

        fieldValue = checkEmptyValue(fieldValue);
        lblValue.setText(fieldValue);
    }

    public void setLabelFontColor(Color color) {
        setLabelFontColor(this, color);
    }

    @Override
    public void setForeground(Color color) {
        setLabelFontColor(this, color);
    }

    private void setLabelFontColor(Control control, Color color) {
        if (control instanceof Composite) {
            Arrays.stream(((Composite) control).getChildren())
                    .forEach(child -> setLabelFontColor(child, color));
        }
        if (control instanceof Label) {
            Label lbl = (Label) control;
            lbl.setForeground(color);
        }
        if (control instanceof StyledText) {
            StyledText lbl = (StyledText) control;
            lbl.setForeground(color);
        }
    }

    private String checkEmptyValue(String fieldValue) {
        if (fieldValue == null || fieldValue.trim().length() == 0) {
            fieldValue = "-";
        } else {
            fieldValue = fieldValue.trim();
        }
        return fieldValue;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
