package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
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

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public EntityModelRow(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(5, false);
        gridLayout.marginBottom = 3;
        gridLayout.marginTop = 3;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        lblEntityIcon = new Label(this, SWT.CENTER);
        lblEntityIcon.setAlignment(SWT.CENTER);
        GridData gd_lblEntityIcon = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 2);
        gd_lblEntityIcon.heightHint = 39;
        gd_lblEntityIcon.widthHint = 40;
        lblEntityIcon.setLayoutData(gd_lblEntityIcon);

        lblEntityId = new Label(this, SWT.NONE);
        lblEntityId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblEntityId.setFont(SWTResourceManager.getBoldFont(lblEntityId.getFont()));

        lblEntityName = new Label(this, SWT.NONE);
        lblEntityName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

        labelTopSpacer = new Label(this, SWT.NONE);
        labelTopSpacer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

        compositeTopDetails = new Composite(this, SWT.NONE);
        compositeTopDetails.setLayout(new RowLayout(SWT.HORIZONTAL));
        compositeTopDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));

        lblEntityDetails = new StyledText(this, SWT.NONE);
        lblEntityDetails.setEnabled(false);
        lblEntityDetails.setEditable(false);
        lblEntityDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));

        labelBottomSpacer = new Label(this, SWT.NONE);
        labelBottomSpacer.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

        compositeBottomDetails = new Composite(this, SWT.NONE);
        compositeBottomDetails.setLayout(new RowLayout(SWT.HORIZONTAL));
        compositeBottomDetails.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
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
        gd_label.heightHint = 15;
        lblSeparator.setLayoutData(gd_label);

        Label lblKey = new Label(composite, SWT.NONE);
        lblKey.setText(fieldName + ": ");

        Label lblValue = new Label(composite, SWT.NONE);
        fieldValue = checkEmptyValue(fieldValue);
        lblValue.setText(fieldValue);
    }

    public void setLabelFontColor(Color color) {
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