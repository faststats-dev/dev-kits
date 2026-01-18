package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingBase;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

final class SimpleTrackingBase implements TrackingBase {
    private final ErrorTracker tracker;

    public SimpleTrackingBase(ErrorTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public Runnable tracked(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }

    @Override
    public <T> PrivilegedAction<T> tracked(PrivilegedAction<T> action) {
        return () -> {
            try {
                return action.run();
            } catch (Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }

    @Override
    public <T> PrivilegedExceptionAction<T> tracked(PrivilegedExceptionAction<T> action) {
        return () -> {
            try {
                return action.run();
            } catch (Throwable error) {
                tracker.trackError(error);
                throw error;
            }
        };
    }
}
