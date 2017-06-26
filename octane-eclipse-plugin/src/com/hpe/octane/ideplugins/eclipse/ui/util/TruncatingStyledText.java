package com.hpe.octane.ideplugins.eclipse.ui.util;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolTip;

public class TruncatingStyledText extends StyledText {

    private String originalText;
    private GC gc;

    public TruncatingStyledText(Composite parent, int style, ToolTip tip) {
        super(parent, style);
        gc = new GC(TruncatingStyledText.this);

        addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                int containerWidth = TruncatingStyledText.this.getSize().x;
                int stringWidth = getTextWidth();

                if (stringWidth > containerWidth) {
                    Point cursorLocation = Display.getCurrent().getCursorLocation();
                    cursorLocation.x += 5;
                    cursorLocation.y += 5;
                    tip.setLocation(cursorLocation);
                    tip.setMessage(originalText);
                    tip.setVisible(true);
                }
            }
        });

        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                int containerWidth = TruncatingStyledText.this.getSize().x;
                int stringWidth = getTextWidth();

                if (stringWidth > containerWidth) {
                    setInternalText(getTrunctated(originalText, containerWidth));
                } else {
                    setInternalText(originalText);
                }
            }
        });

    }

    private int getTextWidth() {
        return gc.stringExtent(originalText).x;

    }

    private int getTextWidth(String text) {
        return gc.stringExtent(text).x;
    }

    private String getTrunctated(String text, int width) {

        int stringWidth = getTextWidth(addEllipsis(text));

        while (stringWidth > width && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
            stringWidth = getTextWidth(addEllipsis(text));
        }

        return addEllipsis(text);
    }

    private String addEllipsis(String text) {
        return text + "...";
    }

    private void setInternalText(String text) {
        super.setText(text);
    }

    @Override
    public void setText(String text) {
        this.originalText = text;
        super.setText(text);
    }

}
