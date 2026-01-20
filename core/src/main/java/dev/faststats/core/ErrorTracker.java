package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingBase;
import dev.faststats.core.concurrent.TrackingExecutors;
import dev.faststats.core.concurrent.TrackingThreadFactory;
import dev.faststats.core.concurrent.TrackingThreadPoolExecutor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * An error tracker.
 *
 * @since 0.10.0
 */
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
     * Attaches an error context to the tracker.
     * <p>
     * If the class loader is {@code null}, the tracker will track all errors.
     *
     * @param loader the class loader
     * @since 0.10.0
     */
    void attachErrorContext(@Nullable ClassLoader loader);

    /**
     * Sets the error event handler which will be called when an error is tracked automatically.
     * <p>
     * The purpose of this handler is to allow custom error handling like logging.
     *
     * @param errorEvent the error event handler
     * @since 0.11.0
     */
    @Contract(mutates = "this")
    void setContextErrorHandler(@Nullable BiConsumer<@Nullable ClassLoader, Throwable> errorEvent);

    /**
     * Returns the error event handler which will be called when an error is tracked automatically.
     *
     * @return the error event handler
     * @since 0.11.0
     */
    @Contract(pure = true)
    Optional<BiConsumer<@Nullable ClassLoader, Throwable>> getContextErrorHandler();

    /**
     * Returns the tracking base.
     *
     * @return the tracking base
     * @since 0.10.0
     */
    @Contract(pure = true)
    TrackingBase base();

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
