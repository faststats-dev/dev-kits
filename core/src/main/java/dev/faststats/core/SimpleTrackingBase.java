package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingBase;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

final class SimpleTrackingBase implements TrackingBase {
    private final ErrorTracker tracker;

    public SimpleTrackingBase(final ErrorTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public Runnable tracked(final Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (final Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }

    @Override
    public <T> PrivilegedAction<T> tracked(final PrivilegedAction<T> action) {
        return () -> {
            try {
                return action.run();
            } catch (final Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }

    @Override
    public <T> PrivilegedExceptionAction<T> tracked(final PrivilegedExceptionAction<T> action) {
        return () -> {
            try {
                return action.run();
            } catch (final Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }
}
