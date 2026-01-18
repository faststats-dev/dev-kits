package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingThreadFactory;

import java.util.concurrent.atomic.AtomicInteger;

final class SimpleTrackingThreadFactory implements TrackingThreadFactory {
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
        var thread = new Thread(this.group, tracker.base().tracked(runnable), name, 0L);
        if (thread.isDaemon()) thread.setDaemon(false);
        if (thread.getPriority() != 5) thread.setPriority(5);
        return thread;
    }
}
