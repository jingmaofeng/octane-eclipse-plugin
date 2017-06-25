package com.hpe.octane.ideplugins.eclipse.util;

import java.util.Timer;
import java.util.TimerTask;

public class DelayedRunnable {

    private Timer fireEventTimer = new Timer();
    private int delay = 500;
    private Runnable runnable;

    public DelayedRunnable(Runnable runnable, int delay) {
        this.delay = delay;
        this.runnable = runnable;
    }

    public void execute() {
        fireEventTimer.cancel();
        fireEventTimer = new Timer();
        fireEventTimer.schedule(createTask(), delay);
    }

    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

}
