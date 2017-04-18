package com.hpe.octane.ideplugins.eclipse.ui.combobox;

public interface CustomEntityComboBoxSelectionListener<T> {
    public void selectionChanged(CustomEntityComboBox<T> customEntityComboBox, T newSelection);
}
