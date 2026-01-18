package dev.faststats.core.concurrent;

import org.jetbrains.annotations.Contract;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

/**
 * Provides tracking for various concurrency-related operations.
 *
 * @since 0.10.0
 */
public interface TrackingBase {
    /**
     * Creates a tracked runnable.
     *
     * @param runnable the runnable
     * @return the tracked runnable
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    Runnable tracked(Runnable runnable);

    /**
     * Creates a tracked action.
     *
     * @param action the action
     * @return the tracked action
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PrivilegedAction<T> tracked(PrivilegedAction<T> action);

    /**
     * Creates a tracked exception action.
     *
     * @param action the exception action
     * @return the tracked exception action
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PrivilegedExceptionAction<T> tracked(PrivilegedExceptionAction<T> action);
}
