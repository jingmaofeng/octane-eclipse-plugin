package com.hpe.octane.ideplugins.eclipse.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

class IconDetail {

    private Color color;
    private String displayLabelText;
    private boolean isOpaque = true;

    public IconDetail(int r, int g, int b, String displayLabelText) {
        this.color = new Color(Display.getCurrent(), r, g, b);
        this.displayLabelText = displayLabelText;
    }

    public IconDetail(Color color, String displayLabelText, boolean isOpaque) {
        this.color = color;
        this.displayLabelText = displayLabelText;
        this.isOpaque = isOpaque;
    }

    public Color getColor() {
        return color;
    }

    public String getDisplayLabelText() {
        return displayLabelText;
    }

    public boolean isOpaque() {
        return isOpaque;
    }

}