package org.androidsocket.Utils;

import org.androidsocket.Interfaces.TimerObserver;

public class UpdateTimer {
    public UpdateTimer(TimerObserver observer, int timeout) {
        this(observer, timeout, false);
    }

    public UpdateTimer(TimerObserver observer, int timeout, boolean start) {
        this.timeout = timeout;
        this.observer = observer;
        if(start) {
            this.start();
        }
    }

    public void start() {
        this.thread = createThread();
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
        this.thread = null;
    }

    public void changeTimer(int timeout) {
        this.timeout = timeout;
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    UpdateTimer.this.observer.onTimerUpdate();
                    try {
                        Thread.sleep(UpdateTimer.this.timeout);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
    }

    private int timeout;
    private Thread thread;
    private TimerObserver observer;
}
