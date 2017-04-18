package com.hpe.octane.ideplugins.eclipse.ui.combobox;

public interface CustomEntityComboBoxLabelProvider<T> {
    public String getSelectedLabel(T entityModelElemnt);

    public String getListLabel(T entityModelElemnt);
}
