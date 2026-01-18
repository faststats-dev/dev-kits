package dev.faststats.errors.concurrent;

import dev.faststats.errors.impl.SimpleTrackingExecutors;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @see java.util.concurrent.Executors
 * @since 0.10.0
 */
public sealed interface TrackingExecutors permits SimpleTrackingExecutors {
    /**
     * @see java.util.concurrent.Executors#newFixedThreadPool(int)
     * @since 0.10.0
     */
    ExecutorService newFixedThreadPool(int threads);

    /**
     * @see java.util.concurrent.Executors#newWorkStealingPool(int)
     * @since 0.10.0
     */
    ExecutorService newWorkStealingPool(int parallelism);

    /**
     * @see java.util.concurrent.Executors#newWorkStealingPool()
     * @since 0.10.0
     */
    ExecutorService newWorkStealingPool();

    /**
     * @see java.util.concurrent.Executors#newFixedThreadPool(int, ThreadFactory)
     * @since 0.10.0
     */
    ExecutorService newFixedThreadPool(int threads, ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#newSingleThreadExecutor()
     * @since 0.10.0
     */
    ExecutorService newSingleThreadExecutor();

    /**
     * @see java.util.concurrent.Executors#newSingleThreadExecutor(ThreadFactory)
     * @since 0.10.0
     */
    ExecutorService newSingleThreadExecutor(ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#newCachedThreadPool()
     * @since 0.10.0
     */
    ExecutorService newCachedThreadPool();

    /**
     * @see java.util.concurrent.Executors#newCachedThreadPool(ThreadFactory)
     * @since 0.10.0
     */
    ExecutorService newCachedThreadPool(ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#newThreadPerTaskExecutor(ThreadFactory)
     * @since 0.10.0
     */
    ExecutorService newThreadPerTaskExecutor(ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#newVirtualThreadPerTaskExecutor()
     * @since 0.10.0
     */
    ExecutorService newVirtualThreadPerTaskExecutor();

    /**
     * @see java.util.concurrent.Executors#newSingleThreadScheduledExecutor()
     * @since 0.10.0
     */
    ScheduledExecutorService newSingleThreadScheduledExecutor();

    /**
     * @see java.util.concurrent.Executors#newSingleThreadScheduledExecutor(ThreadFactory)
     * @since 0.10.0
     */
    ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#newScheduledThreadPool(int)
     * @since 0.10.0
     */
    ScheduledExecutorService newScheduledThreadPool(int corePoolSize);

    /**
     * @see java.util.concurrent.Executors#newScheduledThreadPool(int, ThreadFactory)
     * @since 0.10.0
     */
    ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory factory);

    /**
     * @see java.util.concurrent.Executors#unconfigurableExecutorService(ExecutorService)
     * @since 0.10.0
     */
    ExecutorService unconfigurableExecutorService(ExecutorService executor);

    /**
     * @see java.util.concurrent.Executors#unconfigurableScheduledExecutorService(ScheduledExecutorService)
     * @since 0.10.0
     */
    ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor);

    /**
     * @see java.util.concurrent.Executors#defaultThreadFactory()
     * @since 0.10.0
     */
    ThreadFactory defaultThreadFactory();

    /**
     * @see java.util.concurrent.Executors#callable(Runnable, Object)
     * @since 0.10.0
     */
    <T> Callable<T> callable(Runnable task, T result);

    /**
     * @see java.util.concurrent.Executors#callable(Runnable)
     * @since 0.10.0
     */
    Callable<Object> callable(Runnable task);

    /**
     * @see java.util.concurrent.Executors#callable(PrivilegedAction)
     * @since 0.10.0
     */
    Callable<Object> callable(final PrivilegedAction<?> action);

    /**
     * @see java.util.concurrent.Executors#callable(PrivilegedExceptionAction)
     * @since 0.10.0
     */
    Callable<Object> callable(final PrivilegedExceptionAction<?> action);
}
