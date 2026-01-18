package dev.faststats.errors;

import com.google.gson.JsonArray;
import dev.faststats.errors.concurrent.TrackingExecutors;
import dev.faststats.errors.concurrent.TrackingThreadFactory;
import dev.faststats.errors.concurrent.TrackingThreadPoolExecutor;
import dev.faststats.errors.impl.SimpleErrorTracker;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Optional;

/**
 * An error tracker.
 *
 * @since 0.10.0
 */
// todo: cleanup
public sealed interface ErrorTracker permits SimpleErrorTracker {
    /**
     * Create and attach a new context-aware error tracker.
     * <p>
     * This tracker will automatically track errors that occur in the same class loader as the tracker itself.
     * <p>
     * You can still manually track errors using {@code #trackError}.
     *
     * @return the error tracker
     * @see #contextUnaware()
     * @see #trackError(String)
     * @see #trackError(Throwable)
     * @since 0.10.0
     */
    @Contract(value = " -> new")
    static ErrorTracker contextAware() {
        var tracker = new SimpleErrorTracker();
        tracker.attachErrorContext(ErrorTracker.class.getClassLoader());
        return tracker;
    }

    /**
     * Create a new context-unaware error tracker.
     * <p>
     * This tracker will not automatically track any errors.
     * <p>
     * You have to manually track errors using {@code #trackError}.
     *
     * @return the error tracker
     * @see #contextAware()
     * @see #trackError(String)
     * @see #trackError(Throwable)
     * @since 0.10.0
     */
    @Contract(value = " -> new")
    static ErrorTracker contextUnaware() {
        return new SimpleErrorTracker();
    }

    /**
     * Tracks an error.
     *
     * @param message the error message
     * @see #trackError(Throwable)
     * @since 0.10.0
     */
    @Contract(mutates = "this")
    void trackError(String message);

    /**
     * Tracks an error.
     *
     * @param error the error
     * @since 0.10.0
     */
    @Contract(mutates = "this")
    void trackError(Throwable error);

    /**
     * Gets the error data.
     *
     * @return the error data
     * @since 0.10.0
     */
    @Contract(pure = true)
    Optional<JsonArray> getData(); // todo: keep public?

    /**
     * Clears the error data.
     *
     * @since 0.10.0
     */
    @Contract(mutates = "this")
    void clear(); // todo: keep public?

    /**
     * Attaches an error context to the tracker.
     *
     * @param loader the class loader
     * @since 0.10.0
     */
    void attachErrorContext(@Nullable ClassLoader loader);

    /**
     * Checks if the given error is from the given class loader.
     *
     * @param loader the class loader
     * @param error  the error
     * @return {@code true} if the error is from the given class loader.
     * @since 0.10.0
     */
    @Contract(pure = true)
    boolean isSameLoader(ClassLoader loader, Throwable error); // todo: keep public?

    /**
     * Creates a tracked runnable.
     *
     * @param runnable the runnable
     * @return the tracked runnable
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    Runnable tracked(Runnable runnable); // todo: move to extra interface?

    /**
     * Creates a tracked action.
     *
     * @param action the action
     * @return the tracked action
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PrivilegedAction<T> tracked(PrivilegedAction<T> action); // todo: move to extra interface?

    /**
     * Creates a tracked exception action.
     *
     * @param action the exception action
     * @return the tracked exception action
     * @since 0.10.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PrivilegedExceptionAction<T> tracked(PrivilegedExceptionAction<T> action); // todo: move to extra interface?

    /**
     * Returns the tracking equivalent to {@link java.util.concurrent.Executors}.
     *
     * @return the tracking executors
     * @see java.util.concurrent.Executors
     * @since 0.10.0
     */
    @Contract(pure = true)
    TrackingExecutors executors();

    /**
     * Returns the tracking equivalent to {@link java.util.concurrent.ThreadFactory}.
     *
     * @return the tracking thread factory
     * @see java.util.concurrent.ThreadFactory
     * @since 0.10.0
     */
    @Contract(pure = true)
    TrackingThreadFactory threadFactory();

    /**
     * Returns the tracking equivalent to {@link java.util.concurrent.ThreadPoolExecutor}.
     *
     * @return the tracking thread pool executor
     * @see java.util.concurrent.ThreadPoolExecutor
     * @since 0.10.0
     */
    @Contract(pure = true)
    TrackingThreadPoolExecutor threadPoolExecutor();
}    
