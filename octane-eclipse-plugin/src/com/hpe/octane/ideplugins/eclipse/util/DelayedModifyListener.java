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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Display;

public class DelayedModifyListener implements ModifyListener {

    private Timer fireEventTimer = new Timer();

    private ModifyListener normalModifyListener;
    private int delay = 500;
    private ModifyEvent modifyEvent;

    public DelayedModifyListener(ModifyListener normalModifyListener) {
        this.normalModifyListener = normalModifyListener;
    }

    public DelayedModifyListener(int delay, ModifyListener normalModifyListener) {
        this.normalModifyListener = normalModifyListener;
        this.delay = delay;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        this.modifyEvent = e;
        fireEventTimer.cancel();
        fireEventTimer = new Timer();
        fireEventTimer.schedule(createTask(), delay);
    }

    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Display.getDefault().asyncExec(() -> {
                    normalModifyListener.modifyText(modifyEvent);
                });
            }
        };
    }

}
