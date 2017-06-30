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
package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_AUTHOR;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_DETECTEDBY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ENVIROMENT;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ESTIMATED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ID;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_INVESTED_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_NAME;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_OWNER;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_PHASE;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_RELEASE;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_REMAINING_HOURS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_SEVERITY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_STORY;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_STORYPOINTS;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_SUBTYPE;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_TEST_TYPE;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hpe.adm.octane.ideplugins.services.filtering.Entity;

public class DefaultRowEntityFields {

    private static final String[] commonFields = new String[] { FIELD_ID, FIELD_PHASE, FIELD_NAME };

    private static final String[] progressFields = new String[] {
            FIELD_INVESTED_HOURS,
            FIELD_REMAINING_HOURS,
            FIELD_ESTIMATED_HOURS
    };

    /**
     * The exposed map is immutable
     */
    private static final Map<Entity, Set<String>> tempEntityFields = new HashMap<>();
    public static final Map<Entity, Set<String>> entityFields;

    static {
        // US
        tempEntityFields.put(Entity.USER_STORY, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.USER_STORY), commonFields);
        tempEntityFields.get(Entity.USER_STORY).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.USER_STORY).add(FIELD_RELEASE);
        tempEntityFields.get(Entity.USER_STORY).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.USER_STORY).add(FIELD_STORYPOINTS);
        tempEntityFields.get(Entity.USER_STORY).add(FIELD_OWNER);
        Collections.addAll(tempEntityFields.get(Entity.USER_STORY), progressFields);

        tempEntityFields.put(Entity.QUALITY_STORY, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.QUALITY_STORY), commonFields);
        tempEntityFields.get(Entity.QUALITY_STORY).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.QUALITY_STORY).add(FIELD_RELEASE);
        tempEntityFields.get(Entity.QUALITY_STORY).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.QUALITY_STORY).add(FIELD_STORYPOINTS);
        tempEntityFields.get(Entity.QUALITY_STORY).add(FIELD_OWNER);
        Collections.addAll(tempEntityFields.get(Entity.QUALITY_STORY), progressFields);

        // TASK
        tempEntityFields.put(Entity.TASK, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.TASK), commonFields);
        // entityFields.get(Entity.TASK).add("type"); //not a subtype
        tempEntityFields.get(Entity.TASK).add(FIELD_RELEASE);
        tempEntityFields.get(Entity.TASK).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.TASK).add(FIELD_STORY);
        tempEntityFields.get(Entity.TASK).add(FIELD_OWNER);
        Collections.addAll(tempEntityFields.get(Entity.TASK), progressFields);

        // DEFECT
        tempEntityFields.put(Entity.DEFECT, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.DEFECT), commonFields);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_ENVIROMENT);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_DETECTEDBY);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_STORYPOINTS);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_SEVERITY);
        tempEntityFields.get(Entity.DEFECT).add(FIELD_OWNER);
        Collections.addAll(tempEntityFields.get(Entity.DEFECT), progressFields);

        // GHERKIN_TEST
        tempEntityFields.put(Entity.GHERKIN_TEST, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.GHERKIN_TEST), commonFields);
        tempEntityFields.get(Entity.GHERKIN_TEST).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.GHERKIN_TEST).add(FIELD_TEST_TYPE);
        tempEntityFields.get(Entity.GHERKIN_TEST).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.GHERKIN_TEST).add(FIELD_OWNER);
        tempEntityFields.get(Entity.GHERKIN_TEST).add("automation_status");

        // MANUAL_TEST
        tempEntityFields.put(Entity.MANUAL_TEST, new HashSet<>());
        Collections.addAll(tempEntityFields.get(Entity.MANUAL_TEST), commonFields);
        tempEntityFields.get(Entity.MANUAL_TEST).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.MANUAL_TEST).add(FIELD_TEST_TYPE);
        tempEntityFields.get(Entity.MANUAL_TEST).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.MANUAL_TEST).add(FIELD_OWNER);
        tempEntityFields.get(Entity.MANUAL_TEST).add("steps_num");
        tempEntityFields.get(Entity.MANUAL_TEST).add("automation_status");

        // MANUAL TEST RUNS
        tempEntityFields.put(Entity.MANUAL_TEST_RUN, new HashSet<>());
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add(FIELD_SUBTYPE);
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add(FIELD_NAME);
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add("native_status");
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add(FIELD_ENVIROMENT);
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add("started");
        tempEntityFields.get(Entity.MANUAL_TEST_RUN).add("test_name");

        // TODO: temp
        tempEntityFields.put(Entity.TEST_SUITE_RUN, new HashSet<>());
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add("id");
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add(FIELD_NAME);
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add("native_status");
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add(FIELD_ENVIROMENT);
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add("started");
        tempEntityFields.get(Entity.TEST_SUITE_RUN).add("test_name");

        // COMMENTS
        tempEntityFields.put(Entity.COMMENT, new HashSet<>());
        tempEntityFields.get(Entity.COMMENT).add(FIELD_ID);
        tempEntityFields.get(Entity.COMMENT).add("text");
        tempEntityFields.get(Entity.COMMENT).add(FIELD_AUTHOR);
        tempEntityFields.get(Entity.COMMENT).add("owner_work_item");
        tempEntityFields.get(Entity.COMMENT).add("owner_test");
        tempEntityFields.get(Entity.COMMENT).add("owner_run");

        entityFields = Collections.unmodifiableMap(tempEntityFields);
    }

    private static final Map<Entity, String> subtypeNames = new HashMap<>();
    static {
        subtypeNames.put(Entity.USER_STORY, "User Story");
        subtypeNames.put(Entity.DEFECT, "Defect");
        subtypeNames.put(Entity.QUALITY_STORY, "Quality Story");
        subtypeNames.put(Entity.EPIC, "Epic");
        subtypeNames.put(Entity.FEATURE, "Feature");
        subtypeNames.put(Entity.GHERKIN_TEST, "Gherkin Test");
        subtypeNames.put(Entity.MANUAL_TEST, "Manual Test");
        subtypeNames.put(Entity.MANUAL_TEST_RUN, "Manual Run");
        subtypeNames.put(Entity.TEST_SUITE_RUN, "Test Suite Run");
    }

    public static String getEntityDisplayName(Entity entityType) {
        String subtypeName = subtypeNames.get(entityType);
        return subtypeName != null ? subtypeName : entityType.name().toLowerCase().replace("_", " ");
    }

}
