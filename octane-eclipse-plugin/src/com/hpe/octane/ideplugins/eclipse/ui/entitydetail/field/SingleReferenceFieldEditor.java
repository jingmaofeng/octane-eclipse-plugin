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

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.ReferenceFieldModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;
import com.hpe.octane.ideplugins.eclipse.ui.util.EntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.util.EntityComboBox.EntityLoader;

public class SingleReferenceFieldEditor extends Composite implements FieldEditor {
    
    protected EntityModelWrapper entityModelWrapper;
    protected String fieldName;
    private FieldMessageComposite fieldMessageComposite;
    
    private EntityComboBox combo;

    public SingleReferenceFieldEditor(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        
        combo = new EntityComboBox(
                this, 
                SWT.SINGLE,
                new LabelProvider() {
                    @Override
                    public String getText(Object element) {
                        return Util.getUiDataFromModel((new ReferenceFieldModel("", (EntityModel) element)));
                    }
                },
                new EntityLoader() {
                    @Override
                    public List<EntityModel> loadEntities(String searchQuery) {
                        return null;
                    }
                });

        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        
        fieldMessageComposite = new FieldMessageComposite(this, SWT.NONE);
        fieldMessageComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
    }

    @Override
    public void setField(EntityModelWrapper entityModel, String fieldName) {
        this.entityModelWrapper = entityModel;
        this.fieldName = fieldName;
    }

    @Override
    public void setFieldMessage(FieldMessage fieldMessage) {
        fieldMessageComposite.setFieldMessage(fieldMessage);
    }
    
    @Override
    public FieldMessage getFieldMessage() {
        return fieldMessageComposite.getFieldMessage();
    }
    
}
