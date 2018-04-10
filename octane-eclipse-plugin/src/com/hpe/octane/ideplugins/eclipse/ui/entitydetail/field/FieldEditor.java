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

import org.eclipse.swt.graphics.Drawable;

import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.model.EntityModelWrapper;

public interface FieldEditor extends Drawable {
        
    public enum FieldMessageLevel {
        INFO, ERROR
    }
    
    public static class FieldMessage {
        private FieldMessageLevel fieldMessageLevel;
        private String message;
        
        public FieldMessage(FieldMessageLevel fieldMessageLevel, String message) {
            this.fieldMessageLevel = fieldMessageLevel;
            this.message = message;
        }
        
        public FieldMessageLevel getFieldMessageLevel() {
            return fieldMessageLevel;
        }
        public void setFieldMessageLevel(FieldMessageLevel fieldMessageLevel) {
            this.fieldMessageLevel = fieldMessageLevel;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }   
    }
    
    public void setField(EntityModelWrapper entityModel, String fieldName);
    
    public void setFieldMessage(FieldMessage fieldMessage);
    public FieldMessage getFieldMessage();

}
