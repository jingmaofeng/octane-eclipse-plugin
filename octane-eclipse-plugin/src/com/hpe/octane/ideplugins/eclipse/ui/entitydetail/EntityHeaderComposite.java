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
package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.DefaultEntityFieldsUtil;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.preferences.PluginPreferenceStorage;
import com.hpe.octane.ideplugins.eclipse.ui.comment.job.GetCommentsJob;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.GetPossiblePhasesJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.MultiSelectComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.util.TruncatingStyledText;
import com.hpe.octane.ideplugins.eclipse.ui.util.icon.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.ImageResources;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import com.hpe.octane.ideplugins.eclipse.util.DelayedRunnable;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class EntityHeaderComposite extends Composite {

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(25, 25, 7);

    private static final String TOOLTIP_REFRESH = "Refresh entity details";
    private static final String TOOLTIP_PHASE = "Save changes";
    private static final String TOOLTIP_FIELDS = "Customize fields to be shown";
    private static final String TOOLTIP_COMMENTS = "Show comments";

    private static MetadataService metadataService = Activator.getInstance(MetadataService.class);
    private Map<String, String> prettyFieldsMap;
    private static final Map<Entity, Set<String>> defaultFields = DefaultEntityFieldsUtil.getDefaultFields();

    private ToolTip truncatedLabelTooltip;

    private Label lblEntityIcon;
    private TruncatingStyledText linkEntityName;

    private EntityModel entityModel;

    private EntityPhaseComposite phaseComposite;

    private Button btnRefresh;
    private Button btnSave;
    private Button btnFields;
    private Button btnComments;

    private MultiSelectComboBox<String> fieldCombo;

    private GridData gdBtnComments;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public EntityHeaderComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(8, false));

        Font boldFont = new Font(getDisplay(), new FontData(JFaceResources.DEFAULT_FONT, 12, SWT.BOLD));

        truncatedLabelTooltip = new ToolTip(parent.getShell(), SWT.ICON_INFORMATION);

        lblEntityIcon = new Label(this, SWT.NONE);
        lblEntityIcon.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

        linkEntityName = new TruncatingStyledText(this, SWT.NONE, truncatedLabelTooltip);
        linkEntityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        linkEntityName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
        linkEntityName.addListener(SWT.MouseDown, event -> Activator.getInstance(EntityService.class).openInBrowser(entityModel));

        linkEntityName.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
        linkEntityName.setFont(boldFont);
        linkEntityName.setText("ENTITY_NAME");

        phaseComposite = new EntityPhaseComposite(this, SWT.NONE);
        phaseComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

        btnSave = new Button(this, SWT.NONE);
        btnSave.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        btnSave.setToolTipText(TOOLTIP_PHASE);
        btnSave.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));

        btnRefresh = new Button(this, SWT.NONE);
        btnRefresh.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        btnRefresh.setImage(ImageResources.REFRESH_16X16.getImage());
        btnRefresh.setToolTipText(TOOLTIP_REFRESH);

        btnFields = new Button(this, SWT.NONE);
        btnFields.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        btnFields.setToolTipText(TOOLTIP_FIELDS);

        btnComments = new Button(this, SWT.NONE);
        gdBtnComments = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        btnComments.setLayoutData(gdBtnComments);
        btnComments.setImage(ImageResources.SHOW_COMMENTS.getImage());
        btnComments.setToolTipText(TOOLTIP_COMMENTS);

        // Actual data is populated when entity is set
        fieldCombo = new MultiSelectComboBox<>(new LabelProvider() {
            @Override
            public String getText(Object fieldName) {
                return prettyFieldsMap.get(fieldName);
            }
        });

        btnFields.addListener(SWT.Selection, event -> {
            fieldCombo.showFloatShell(btnFields);
            fieldCombo.setSelection(PluginPreferenceStorage.getShownEntityFields(Entity.getEntityType(entityModel)), false);
        });

        fieldCombo.setResetRunnable(() -> {
            fieldCombo.setSelection(defaultFields.get(Entity.getEntityType(entityModel)));
        });

        DelayedRunnable delayedRunnable = new DelayedRunnable(() -> {
            Display.getDefault().asyncExec(() -> {

                PluginPreferenceStorage.setShownEntityFields(
                        Entity.getEntityType(entityModel),
                        new LinkedHashSet<>(fieldCombo.getSelections()));

                if (PluginPreferenceStorage.areShownEntityFieldsDefaults(Entity.getEntityType(entityModel))) {
                    btnFields.setImage(ImageResources.FIELDS_OFF.getImage());
                } else {
                    btnFields.setImage(ImageResources.FIELDS_ON.getImage());
                }
            });
        }, 500);

        fieldCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                delayedRunnable.execute();
            }
        });
    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        phaseComposite.setEntityModel(entityModel);
        lblEntityIcon.setImage(entityIconFactory.getImageIcon(Entity.getEntityType(entityModel)));
        linkEntityName.setText(entityModel.getValue(EntityFieldsConstants.FIELD_NAME).getValue().toString());
        selectFieldsToDisplay(entityModel);

        if (GetCommentsJob.hasCommentSupport(Entity.getEntityType(entityModel))) {
            btnComments.setVisible(true);
            gdBtnComments.exclude = false;
        } else {
            btnComments.setVisible(false);
            gdBtnComments.exclude = true;
        }

        if (GetPossiblePhasesJob.hasPhases(Entity.getEntityType(entityModel))) {
            setChildVisibility(phaseComposite, true);
        } else {
            setChildVisibility(phaseComposite, false);
        }
    }

    private void setChildVisibility(Control control, boolean isVisible) {
        control.setVisible(isVisible);
    }

    public void addSaveSelectionListener(Listener listener) {
        btnSave.addListener(SWT.Selection, listener);
    }

    public void addRefreshSelectionListener(Listener listener) {
        btnRefresh.addListener(SWT.Selection, listener);
    }

    public void addCommentsSelectionListener(Listener listener) {
        btnComments.addListener(SWT.Selection, listener);
    }

    private void selectFieldsToDisplay(EntityModel entityModel) {
        if (PluginPreferenceStorage.areShownEntityFieldsDefaults(Entity.getEntityType(entityModel))) {
            btnFields.setImage(ImageResources.FIELDS_OFF.getImage());
        } else {
            btnFields.setImage(ImageResources.FIELDS_ON.getImage());
        }

        // make a map of the field names and labels
        Collection<FieldMetadata> allFields = metadataService.getVisibleFields(Entity.getEntityType(entityModel));
        allFields.stream()
                .filter(f -> !f.getName().equals(EntityFieldsConstants.FIELD_DESCRIPTION)
                        && !f.getName().equals(EntityFieldsConstants.FIELD_NAME)
                        && !f.getName().equals(EntityFieldsConstants.FIELD_PHASE));
        prettyFieldsMap = allFields.stream().collect(Collectors.toMap(FieldMetadata::getName, FieldMetadata::getLabel));

        fieldCombo.clear();
        fieldCombo.addAll(prettyFieldsMap.keySet());
        fieldCombo.setSelection(PluginPreferenceStorage.getShownEntityFields(Entity.getEntityType(entityModel)), false);
    }

}
