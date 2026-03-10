package com.alsaev.utils;

public final class Timer {
    private long startTime = -1;
    private long finishTime = -1;
    private boolean isRunning = false;

    public void start() {
        startTime = System.nanoTime();
        isRunning = true;
    }

    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Таймер не был запущен!");
        }
        finishTime = System.nanoTime();
        isRunning = false;
    }

    public long getDurationNano() {
        if (startTime == -1) throw new IllegalStateException("Замер не был начат!");
        if (isRunning) throw new IllegalStateException("Таймер ещё работает!");
        return finishTime - startTime;
    }

    public double getDurationMs() {
        return getDurationNano() / 1_000_000.0;
    }

    public double getDurationSec() {
        return getDurationMs() / 1000.0;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }
}
