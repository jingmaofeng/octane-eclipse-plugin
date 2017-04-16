package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.rowrenderer;

import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_AUTHOR;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_DETECTEDBY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ENVIROMENT;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ESTIMATED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_INVESTED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_REMAINING_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_SEVERITY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_STORYPOINTS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_TEST_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRenderer;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.EntityModelRow.DetailsPosition;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class MyWorkEntityModelRowRenderer implements EntityModelRenderer {

    // Re-usable field setters
    static IdFieldSetter fsId = new IdFieldSetter();
    static NameFieldSetter fsName = new NameFieldSetter();
    static IconFieldSetter fsIcon = new IconFieldSetter();

    // Top
    static RowFieldSetter fsStoryPoints = new GenericFieldSetter(FIELD_STORYPOINTS, "SP", DetailsPosition.TOP);
    static RowFieldSetter fsAuthor = new GenericFieldSetter(FIELD_AUTHOR, "Author", DetailsPosition.TOP);
    static RowFieldSetter fsOwner = new GenericFieldSetter("owner", "Owner", DetailsPosition.TOP);
    static RowFieldSetter fsPhase = new GenericFieldSetter("phase", "Phase", DetailsPosition.TOP);
    static RowFieldSetter fsStatus = new GenericFieldSetter("status", "Status", DetailsPosition.TOP);

    // Bottom
    static RowFieldSetter fsAutomationStatus = new GenericFieldSetter("automation_status", "Automation status", DetailsPosition.BOTTOM);
    static RowFieldSetter fsStarted = new GenericFieldSetter("started", "Started", DetailsPosition.BOTTOM);

    static RowFieldSetter fsInvestedHours = new GenericFieldSetter(FIELD_INVESTED_HOURS, "Invested hours", DetailsPosition.BOTTOM);
    static RowFieldSetter fsRemainingHours = new GenericFieldSetter(FIELD_REMAINING_HOURS, "Remaining hours", DetailsPosition.BOTTOM);
    static RowFieldSetter fsEstimatedHours = new GenericFieldSetter(FIELD_ESTIMATED_HOURS, "Estimated hours", DetailsPosition.BOTTOM);

    static RowFieldSetter fsSeverity = new GenericFieldSetter("serverity", "Phase", DetailsPosition.TOP);
    static RowFieldSetter fsDetecedBy = new GenericFieldSetter(FIELD_DETECTEDBY, "Phase", DetailsPosition.TOP);

    // Subtile
    static RowFieldSetter fsEnvironment = new GenericFieldSetter(FIELD_ENVIROMENT, "[No environment]");
    static RowFieldSetter fsRelease = new GenericFieldSetter("release", "[No release]");
    static RowFieldSetter fsTestType = new GenericFieldSetter(FIELD_TEST_TYPE, "");

    /**
     * Describe the way the fields are set into the row composite. <br>
     * This map is also used to determine what fields are needed for the rest
     * call, when getting the entities. <br>
     * The detail fields are added from right to left
     */
    private static Map<Entity, Collection<RowFieldSetter>> fieldSetterMap = new LinkedHashMap<>();
    static {
        fieldSetterMap.put(Entity.USER_STORY, asList(
                // top
                fsPhase,
                fsStoryPoints,
                fsOwner,
                fsAuthor,
                // bottom
                fsInvestedHours,
                fsRemainingHours,
                fsEstimatedHours,
                // subtitle
                fsRelease));

        fieldSetterMap.put(Entity.QUALITY_STORY, asList(
                // top
                fsPhase,
                fsStoryPoints,
                fsOwner,
                fsAuthor,
                // bottom
                fsInvestedHours,
                fsRemainingHours,
                fsEstimatedHours,
                // subtitle
                fsRelease));

        fieldSetterMap.put(Entity.DEFECT, asList(
                // top
                fsPhase,
                fsStoryPoints,
                fsOwner,
                new GenericFieldSetter(FIELD_SEVERITY, "Severity", DetailsPosition.TOP),
                new GenericFieldSetter(FIELD_DETECTEDBY, "Detected by", DetailsPosition.TOP),
                // bottom
                fsInvestedHours,
                fsRemainingHours,
                fsEstimatedHours,
                // subtitle
                fsEnvironment));

        fieldSetterMap.put(Entity.TASK, asList(
                // top
                fsPhase,
                fsOwner,
                fsAuthor,
                // bottom
                fsInvestedHours,
                fsRemainingHours,
                fsEstimatedHours,
                // subtitle
                new TaskSubtitleRowFieldSetter()));

        fieldSetterMap.put(Entity.MANUAL_TEST, asList(
                // top
                fsPhase,
                fsOwner,
                fsAuthor,
                // bottom
                new GenericFieldSetter("steps_num", "Steps", DetailsPosition.BOTTOM),
                fsAutomationStatus,
                // subtitle
                fsTestType));

        fieldSetterMap.put(Entity.GHERKIN_TEST, asList(
                // top
                fsPhase,
                fsOwner,
                fsAuthor,
                // bottom
                fsAutomationStatus,
                // subtitle
                fsTestType));

        fieldSetterMap.put(Entity.TEST_SUITE_RUN, asList(
                // top
                fsStatus,
                // bottom
                fsStarted,
                // subtitle
                fsEnvironment));

        fieldSetterMap.put(Entity.MANUAL_TEST_RUN, asList(
                // top
                fsStatus,
                // bottom
                fsStarted,
                // subtitle
                fsEnvironment));

        fieldSetterMap.put(Entity.COMMENT, asList(
                new CommentFieldSetter(), // sets the name and subtitle
                fsAuthor));

        // Add common details
        fieldSetterMap.forEach((entityType, fieldSetters) -> {
            fieldSetters.add(fsIcon);

            // Add ID and Name field setters for everything except the COMMENT
            if (entityType != Entity.COMMENT) {
                fieldSetters.add(fsId);
                fieldSetters.add(fsName);
            }
        });
    }

    @Override
    public EntityModelRow createRow(Composite parent, EntityModel userItem) {

        final EntityModel entityModel = MyWorkUtil.getEntityModelFromUserItem(userItem);
        Entity entityType = Entity.getEntityType(entityModel);

        final EntityModelRow rowComposite = new EntityModelRow(parent, SWT.NONE);
        rowComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        rowComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        // Show dismissible if needed
        if (MyWorkUtil.isUserItemDismissible(userItem)) {
            rowComposite.addDetails("", "Dismissible", DetailsPosition.BOTTOM);
        }

        // Setup row based on field setters
        Collection<RowFieldSetter> fieldSetters = fieldSetterMap.get(entityType);
        fieldSetters.forEach(fs -> fs.setField(rowComposite, entityModel));

        return rowComposite;
    }

    public static Map<Entity, Set<String>> getRequiredFields() {

        Map<Entity, Set<String>> result = new HashMap<>();

        fieldSetterMap.forEach((key, fieldSetters) -> {
            result.put(
                    key,
                    fieldSetters.stream().flatMap(fs -> Arrays.stream(fs.getFieldNames())).collect(Collectors.toSet()));
        });

        return result;
    }

    private static ArrayList<RowFieldSetter> asList(RowFieldSetter... fieldSetters) {
        ArrayList<RowFieldSetter> result = new ArrayList<RowFieldSetter>(Arrays.asList(fieldSetters));
        return result;
    }

}