package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingThreadFactory;

import java.util.concurrent.ThreadFactory;

final class WrappedTrackingThreadFactory implements ThreadFactory {
    private final ErrorTracker tracker;
    private final ThreadFactory factory;

    public WrappedTrackingThreadFactory(final ErrorTracker tracker, final ThreadFactory factory) {
        this.tracker = tracker;
        this.factory = factory;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        return factory.newThread(tracker.base().tracked(runnable));
    }

    public static ThreadFactory wrap(final ErrorTracker tracker, final ThreadFactory factory) {
        return factory instanceof TrackingThreadFactory || factory instanceof WrappedTrackingThreadFactory
                ? factory : new WrappedTrackingThreadFactory(tracker, factory);
    }
}
