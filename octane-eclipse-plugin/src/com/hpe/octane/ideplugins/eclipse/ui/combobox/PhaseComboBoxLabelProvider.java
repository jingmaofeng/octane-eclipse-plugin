package com.hpe.octane.ideplugins.eclipse.ui.combobox;

public interface PhaseComboBoxLabelProvider<T> {
    public String getSelectedLabel(T element);

    public String getListLabel(T element);
}
