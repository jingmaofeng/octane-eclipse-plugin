/*******************************************************************************
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.util;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public final class InfoPopup extends PopupDialog {

    private int width = 200;
    private int height = 70;

    private Text text;
    private String content = "";
    private boolean autoClose = true;
    private long timeToClose = 3000;

    public InfoPopup(String headerString, String content) {
        this(headerString, null, content);
    }

    public InfoPopup(String headerString, String content, int width, int height) {
        this(headerString, null, content);
        this.width = width;
        this.height = height;
        this.timeToClose = 7000;
    }

    public InfoPopup(String headerString, String footerString, String content) {
        this(headerString, footerString, content, true);
    }

    public InfoPopup(String headerString, String footerString, String content, boolean autoclose) {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                PopupDialog.HOVER_SHELLSTYLE,
                false,
                false,
                false,
                true,
                false,
                headerString,
                footerString);

        this.content = content;
        this.autoClose = autoclose;
    }

    private void scheduleClose() {
        TimerTask timer = new TimerTask() {
            @Override
            public void run() {
                Display.getDefault().asyncExec(() -> {
                    close();
                });
            }
        };
        Timer fireEventTimer = new Timer();
        fireEventTimer.schedule(timer, timeToClose);
    }

    @Override
    public int open() {
        if (autoClose) {
            scheduleClose();
        }
        return super.open();
    }

    @Override
    protected Control createTitleMenuArea(Composite arg0) {
        Control ctrl = super.createTitleMenuArea(arg0);
        return ctrl;
    }

    @Override
    protected void fillDialogMenu(IMenuManager dialogMenu) {
        dialogMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager arg0) {
                handleShellCloseEvent();
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.NO_FOCUS);
        text.setText(content);
        return text;
    }

    /**
     * Place the dialog in the corner of the IDE
     */
    @Override
    protected void adjustBounds() {
        Rectangle shellBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
        shellBounds.x = shellBounds.x + shellBounds.width - width - 20;
        shellBounds.y = shellBounds.y + shellBounds.height - height - 20;
        shellBounds.width = width;
        shellBounds.height = height;
        getShell().setBounds(shellBounds);
    }

    public void setText(String textContents) {
        this.content = textContents;
    }

}
