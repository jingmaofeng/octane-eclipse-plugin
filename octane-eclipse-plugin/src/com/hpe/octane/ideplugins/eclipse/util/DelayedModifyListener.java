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
