package com.hpe.octane.ideplugins.eclipse.ui.entitydetail.fieldeditor;

import org.eclipse.swt.graphics.Drawable;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.ObservableEntityModel;

public interface FieldEditor extends Drawable {
    
    public void setField(ObservableEntityModel entityModel, String fieldName);
    public void forceUpdate();
    public boolean hasError();
    public String getErrorText();
    
}