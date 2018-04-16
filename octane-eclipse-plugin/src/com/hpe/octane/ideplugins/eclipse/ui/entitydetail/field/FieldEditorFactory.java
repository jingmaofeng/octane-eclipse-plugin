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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.metadata.FieldMetadata;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.MetadataService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public class FieldEditorFactory {

    private MetadataService metadataService = Activator.getInstance(MetadataService.class);

    public FieldEditor createFieldEditor(Composite parent, EntityModelWrapper entityModelWrapper, String fieldName) {

        EntityModel entityModel = entityModelWrapper.getReadOnlyEntityModel();
        Entity entityType = Entity.getEntityType(entityModel);
        FieldMetadata fieldMetadata = metadataService.getMetadata(entityType, fieldName);

        FieldEditor fieldEditor = null;

        if (!fieldMetadata.isEditable()) {
            fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);

        } else {
            switch (fieldMetadata.getFieldType()) {
                case Integer:
                    fieldEditor = new NumericFieldEditor(parent, SWT.NONE, false);
                    ((NumericFieldEditor) fieldEditor).setBounds(0, Long.MAX_VALUE);
                    break;
                case Float:
                    fieldEditor = new NumericFieldEditor(parent, SWT.NONE, true);
                    break;
                case String:
                    fieldEditor = new StringFieldEditor(parent, SWT.BORDER);
                    break;
                case Boolean:
                    fieldEditor = new BooleanFieldEditor(parent, SWT.NONE);
                    break;
                case DateTime:
                    fieldEditor = new DateTimeFieldEditor(parent, SWT.NONE);
                    break;
                case Reference:
                    fieldEditor = new ReferenceFieldEditor(parent, SWT.NONE);
                    break;
                default:
                    fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);
                    break;
            }

        }
        try {
            fieldEditor.setField(entityModelWrapper, fieldName);
        } catch (Exception ex) {
            ILog log = Activator.getDefault().getLog();
            StringBuilder sbMessage = new StringBuilder();
            sbMessage.append("Faied to set field ")
                    .append(fieldName)
                    .append(" in detail tab for entity ")
                    .append(entityModel.getId())
                    .append(": ")
                    .append(ex.getMessage());

            log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, sbMessage.toString()));

            fieldEditor = new ReadOnlyFieldEditor(parent, SWT.NONE);
        }
        return fieldEditor;
    }

}
