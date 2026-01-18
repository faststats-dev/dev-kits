package dev.faststats.errors.impl;

import dev.faststats.errors.ErrorTracker;
import dev.faststats.errors.concurrent.TrackingThreadFactory;

import java.util.concurrent.ThreadFactory;

public final class WrappedTrackingThreadFactory implements ThreadFactory {
    private final ErrorTracker tracker;
    private final ThreadFactory factory;

    public WrappedTrackingThreadFactory(ErrorTracker tracker, ThreadFactory factory) {
        this.tracker = tracker;
        this.factory = factory;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return factory.newThread(tracker.tracked(runnable));
    }

    public static ThreadFactory wrap(ErrorTracker tracker, ThreadFactory factory) {
        return factory instanceof TrackingThreadFactory || factory instanceof WrappedTrackingThreadFactory
                ? factory : new WrappedTrackingThreadFactory(tracker, factory);
    }
}
