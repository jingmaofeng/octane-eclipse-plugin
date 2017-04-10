package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import static com.hpe.adm.octane.services.util.Util.getContainerItemForCommentModel;
import static com.hpe.adm.octane.services.util.Util.getUiDataFromModel;
import static com.hpe.octane.ideplugins.eclipse.ui.entitylist.DefaultRowEntityFields.getSubtypeName;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_AUTHOR;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_DETECTEDBY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ENVIROMENT;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ESTIMATED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_FULL_NAME;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ID;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_INVESTED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_RELEASE;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_REMAINING_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_SEVERITY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_STORYPOINTS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_TEST_RUN_NATIVE_STATUS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_TEST_RUN_STARTED_DATE;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_TEST_TYPE;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow.DetailsPosition;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;

class DefaultEntityModelRenderer implements EntityModelRenderer {

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(40, 40, 13);

    @Override
    public EntityModelRow createRow(Composite parent, EntityModel entityModel) {

        Entity entityType = Entity.getEntityType(entityModel);
        Integer entityId = Integer.valueOf(Util.getUiDataFromModel(entityModel.getValue(FIELD_ID)));

        // Init row panel
        final EntityModelRow rowPanel = new EntityModelRow(parent, SWT.NONE);
        rowPanel.setBackgroundMode(SWT.INHERIT_FORCE);
        rowPanel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        // if (selected && hasFocus) {
        // rowPanel = new EntityModelRow(new Color(255, 255, 255));
        // } else {
        // rowPanel = new EntityModelRow();
        // }
        // rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
        // JBColor.border()));

        rowPanel.setEntityIcon(entityIconFactory.getImageIcon(entityType));

        // Add owner if entity is not owned by the current plugin user
        String ownerText = null;

        // check who the entity belongs too if the owner field exits
        if (entityModel.getValue("owner") != null) {
            if (entityModel.getValue("owner").getValue() == null) {
                ownerText = "";
            } else {
                EntityModel ownerEntity = (EntityModel) entityModel.getValue("owner").getValue();
                ownerText = getUiDataFromModel(ownerEntity.getValue(FIELD_FULL_NAME));
            }
        }

        if (ownerText != null) {
            rowPanel.addDetails(
                    "Owner",
                    ownerText,
                    DetailsPosition.TOP);
        }

        // Add specific details for each item type

        if (entityType != Entity.COMMENT) {
            if (entityType.equals(Entity.MANUAL_TEST_RUN) || entityType.equals(Entity.TEST_SUITE_RUN)) {
                String nativeStatus = getUiDataFromModel(entityModel.getValue(FIELD_TEST_RUN_NATIVE_STATUS));
                rowPanel.addDetails("Status", nativeStatus, DetailsPosition.TOP);
            } else {
                String phase = getUiDataFromModel(entityModel.getValue("phase"));
                rowPanel.addDetails("Phase", phase, DetailsPosition.TOP);
            }

            rowPanel.setEntityId(entityId);
            rowPanel.setEntityName(getUiDataFromModel(entityModel.getValue("name")));
        }

        if (Entity.DEFECT.equals(entityType)) {
            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_ENVIROMENT)),
                    "No environment");

            addStoryPoints(rowPanel, entityModel);
            rowPanel.addDetails("Detected by", getUiDataFromModel(entityModel.getValue(FIELD_DETECTEDBY)), DetailsPosition.TOP);
            rowPanel.addDetails("Severity", getUiDataFromModel(entityModel.getValue(FIELD_SEVERITY)), DetailsPosition.TOP);
            addProgress(rowPanel, entityModel);

        } else if (Entity.USER_STORY.equals(entityType) || Entity.QUALITY_STORY.equals(entityType)) {
            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_RELEASE)),
                    "No release");

            addStoryPoints(rowPanel, entityModel);
            addAuthor(rowPanel, entityModel);
            addProgress(rowPanel, entityModel);

        } else if (Entity.TASK.equals(entityType)) {

            // Add parent details for tasks
            EntityModel storyEntityModel = (EntityModel) entityModel.getValue("story").getValue();

            String type;
            if (storyEntityModel.getValue("subtype") != null) {
                type = storyEntityModel.getValue("subtype").getValue().toString();
            } else {
                type = storyEntityModel.getValue("type").getValue().toString();
            }
            String storyTypeName = getSubtypeName(type);

            StringBuilder parentInfoSb = new StringBuilder();
            parentInfoSb.append("<html>");
            parentInfoSb.append("Task of " + storyTypeName.toLowerCase());
            parentInfoSb.append(" <b>" + storyEntityModel.getValue("id").getValue().toString() + ":</b>");
            parentInfoSb.append(" " + storyEntityModel.getValue("name").getValue().toString());
            parentInfoSb.append("</html>");
            rowPanel.setEntitySubTitle(parentInfoSb.toString(), "no parent");

            addAuthor(rowPanel, entityModel);
            addProgress(rowPanel, entityModel);

        } else if (Entity.GHERKIN_TEST.equals(entityType)) {

            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_TEST_TYPE)),
                    "");
            addAuthor(rowPanel, entityModel);
            rowPanel.addDetails("Automation status",
                    getUiDataFromModel(entityModel.getValue("automation_status")),
                    DetailsPosition.BOTTOM);

            // addProgress(rowPanel, entityModel);

        } else if (Entity.MANUAL_TEST.equals(entityType)) {

            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_TEST_TYPE)),
                    "");

            addAuthor(rowPanel, entityModel);
            rowPanel.addDetails("Steps", getUiDataFromModel(entityModel.getValue("steps_num")), DetailsPosition.BOTTOM);
            String automationStatus = getUiDataFromModel(entityModel.getValue("automation_status"));
            if (StringUtils.isNotEmpty(automationStatus)) {
                rowPanel.addDetails("Automation status", automationStatus, DetailsPosition.BOTTOM);
            }
        } else if (Entity.COMMENT.equals(entityType)) {

            String text = getUiDataFromModel(entityModel.getValue("text"));
            text = " Comment: " + Util.stripHtml(text);
            String author = getUiDataFromModel(entityModel.getValue(FIELD_AUTHOR), FIELD_FULL_NAME);
            FieldModel owner = getContainerItemForCommentModel(entityModel);
            String ownerId = getUiDataFromModel(owner, "id");
            String ownerName = getUiDataFromModel(owner, "name");
            String ownerSubtype = getUiDataFromModel(owner, "subtype");

            String entityName = "Appears in " + getSubtypeName(ownerSubtype) + ": " + "<b>" + ownerId + "</b>" + " " + ownerName;

            rowPanel.setEntityName(entityName);
            rowPanel.setEntitySubTitle(text, "");
            rowPanel.addDetails("Author", author, DetailsPosition.TOP);

        } else if (Entity.MANUAL_TEST_RUN.equals(entityType)) {

            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_ENVIROMENT)),
                    "No environment");

            addAuthor(rowPanel, entityModel);
            rowPanel.addDetails("Started", getUiDataFromModel(entityModel.getValue(FIELD_TEST_RUN_STARTED_DATE)), DetailsPosition.BOTTOM);

        } else if (Entity.TEST_SUITE_RUN.equals(entityType)) {

            rowPanel.setEntitySubTitle(
                    getUiDataFromModel(entityModel.getValue(FIELD_ENVIROMENT)),
                    "No environment");

            rowPanel.addDetails("Author", getUiDataFromModel(entityModel.getValue(FIELD_AUTHOR), FIELD_FULL_NAME), DetailsPosition.TOP);
            rowPanel.addDetails("Started", getUiDataFromModel(entityModel.getValue(FIELD_TEST_RUN_STARTED_DATE)), DetailsPosition.BOTTOM);
        }

        return rowPanel;
    }

    private void addProgress(EntityModelRow rowPanel, EntityModel entityModel) {
        rowPanel.addDetails("Invested hours", getUiDataFromModel(entityModel.getValue(FIELD_INVESTED_HOURS)), DetailsPosition.BOTTOM);
        rowPanel.addDetails("Remaining hours", getUiDataFromModel(entityModel.getValue(FIELD_REMAINING_HOURS)), DetailsPosition.BOTTOM);
        rowPanel.addDetails("Estimated hours", getUiDataFromModel(entityModel.getValue(FIELD_ESTIMATED_HOURS)), DetailsPosition.BOTTOM);
    }

    private void addStoryPoints(EntityModelRow entityModelRow, EntityModel entityModel) {
        String storyPoints = getUiDataFromModel(entityModel.getValue(FIELD_STORYPOINTS));
        entityModelRow.addDetails("SP", storyPoints, DetailsPosition.TOP);
    }

    private void addAuthor(EntityModelRow entityModelRow, EntityModel entityModel) {
        String storyPoints = getUiDataFromModel(entityModel.getValue(FIELD_AUTHOR));
        entityModelRow.addDetails("Author", storyPoints, DetailsPosition.TOP);
    }

}
