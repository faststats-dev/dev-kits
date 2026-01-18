package dev.faststats.errors.concurrent;

import dev.faststats.errors.impl.SimpleTrackingThreadFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;

import java.util.concurrent.ThreadFactory;

/**
 * Creates threads that are tracked by the error tracker.
 * 
 * @see java.util.concurrent.ThreadFactory
 * @since 0.10.0
 */
public sealed interface TrackingThreadFactory extends ThreadFactory permits SimpleTrackingThreadFactory {
    /**
     * Creates a new thread for the given runnable.
     * <p>
     * The resulting thread will be tracked by the error tracker.
     *
     * @param runnable The runnable to execute in the new thread.
     * @return The newly created thread.
     * @see ThreadFactory#newThread(Runnable)
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    Thread newThread(Runnable runnable);

    /**
     * Creates a new named thread for the given runnable.
     * <p>
     * The resulting thread will be tracked by the error tracker.
     *
     * @param name     The name of the new thread.
     * @param runnable The runnable to execute in the new thread.
     * @return The newly created named thread.
     * @since 0.10.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    Thread newThread(@NonNls String name, Runnable runnable);
}
