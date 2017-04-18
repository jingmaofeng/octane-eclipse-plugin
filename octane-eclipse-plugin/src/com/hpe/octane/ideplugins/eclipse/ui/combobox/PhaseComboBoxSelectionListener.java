package com.hpe.octane.ideplugins.eclipse.ui.combobox;

public interface PhaseComboBoxSelectionListener<T> {
	 public void selectionChanged(PhaseComboBox<T> typedComboBox, T newSelection);
}
