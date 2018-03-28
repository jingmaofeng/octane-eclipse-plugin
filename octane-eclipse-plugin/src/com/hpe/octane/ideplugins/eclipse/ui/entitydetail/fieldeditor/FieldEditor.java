package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

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
    
    public void setField(EntityModelWrapper entityModel, String... fieldNames);
    
    public void forceUpdate();
    
    public void setFieldMessage(FieldMessage fieldMessage);
    public void getFieldMessage(FieldMessage fieldMessage);

}