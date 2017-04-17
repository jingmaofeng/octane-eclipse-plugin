package com.hpe.octane.ideplugins.eclipse.util;

import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.widgets.Control;

public interface ControlProvider<T extends Drawable> {
    T createControl(Control parent);
}