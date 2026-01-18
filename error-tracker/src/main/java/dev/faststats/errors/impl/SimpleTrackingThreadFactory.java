package dev.faststats.errors.impl;

import dev.faststats.errors.ErrorTracker;
import dev.faststats.errors.concurrent.TrackingThreadFactory;

import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleTrackingThreadFactory implements TrackingThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final ErrorTracker tracker;

    public SimpleTrackingThreadFactory(ErrorTracker tracker) {
        this.tracker = tracker;
        this.group = Thread.currentThread().getThreadGroup();
        this.namePrefix = "tracking-pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return newThread(namePrefix + threadNumber.getAndIncrement(), runnable);
    }

    @Override
    public Thread newThread(String name, Runnable runnable) {
        var thread = new Thread(this.group, tracker.tracked(runnable), name, 0L);
        if (thread.isDaemon()) thread.setDaemon(false);
        if (thread.getPriority() != 5) thread.setPriority(5);
        return thread;
    }
}
