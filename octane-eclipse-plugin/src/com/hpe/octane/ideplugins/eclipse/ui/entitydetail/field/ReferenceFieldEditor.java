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

import java.util.Collection;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.MultiReferenceFieldModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.EntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.util.EntityComboBox.EntityLoader;

public class ReferenceFieldEditor extends Composite implements FieldEditor {

    protected EntityModelWrapper entityModelWrapper;
    protected String fieldName;
    private EntityComboBox entityComboBox;

    public ReferenceFieldEditor(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);

        entityComboBox = new EntityComboBox(this, SWT.NONE);
        entityComboBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        entityComboBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (entityComboBox.getSelectionMode() == SWT.MULTI) {
                    entityModelWrapper.setValue(new MultiReferenceFieldModel(fieldName, entityComboBox.getSelectedEntities()));
                } else {
                    entityModelWrapper.setValue(new ReferenceFieldModel(fieldName, entityComboBox.getSelectedEntity()));
                }
            }
        });
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {

        this.entityModelWrapper = entityModel;
        this.fieldName = fieldName;

        @SuppressWarnings("rawtypes")
        FieldModel fieldModel = entityModel.getValue(fieldName);

        if (fieldModel != null && fieldModel.getValue() != null) {

            if (fieldModel instanceof ReferenceFieldModel && entityComboBox.getSelectionMode() == SWT.SINGLE) {
                entityComboBox.setSelectedEntity(((ReferenceFieldModel) fieldModel).getValue());

            } else if (fieldModel instanceof MultiReferenceFieldModel && entityComboBox.getSelectionMode() == SWT.MULTI) {
                entityComboBox.setSelectedEntities(((MultiReferenceFieldModel) fieldModel).getValue());

            } else {

                throw new RuntimeException("Failed to set value of the Reference field model, field value and metadata not compatible");
            }

        } else {
            entityComboBox.clearSelection();
        }
    }

    public void setLabelProvider(LabelProvider labelProvider) {
        entityComboBox.setLabelProvider(labelProvider);
    }

    public void setEntityLoader(EntityLoader entityLoader) {
        entityComboBox.setEntityLoader(entityLoader);
    }

    public void setSelectionMode(int selectionMode) {
        entityComboBox.setSelectionMode(selectionMode);
    }

    public void setSelectedEntities(Collection<EntityModel> entityModel) {
        entityComboBox.setSelectedEntities(entityModel);
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {
    }

    @Override
    public FieldMessage getFieldMessage() {
        return null;
    }

}