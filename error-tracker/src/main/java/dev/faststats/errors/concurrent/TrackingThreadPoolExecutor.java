package dev.faststats.errors.concurrent;

import dev.faststats.errors.impl.SimpleTrackingThreadPoolExecutor;
import org.jetbrains.annotations.Range;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Creates thread pool executors that are tracked by the error tracker.
 *
 * @see java.util.concurrent.ThreadPoolExecutor
 * @since 0.10.0
 */
public sealed interface TrackingThreadPoolExecutor permits SimpleTrackingThreadPoolExecutor {
    /**
     * Creates a new thread pool executor.
     * <p>
     * The resulting executor will be tracked by the error tracker.
     *
     * @param corePoolSize    The core pool size.
     * @param maximumPoolSize The maximum pool size.
     * @param keepAliveTime   The keep alive time.
     * @param unit            The time unit.
     * @param workQueue       The work queue.
     * @return The newly created thread pool executor.
     * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue)
     * @since 0.10.0
     */
    ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue);

    /**
     * Creates a new thread pool executor.
     *
     * @param corePoolSize    The core pool size.
     * @param maximumPoolSize The maximum pool size.
     * @param keepAliveTime   The keep alive time.
     * @param unit            The time unit.
     * @param workQueue       The work queue.
     * @param threadFactory   The thread factory.
     * @return The newly created thread pool executor.
     * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
     * @since 0.10.0
     */
    ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory);

    /**
     * Creates a new thread pool executor.
     *
     * @param corePoolSize    The core pool size.
     * @param maximumPoolSize The maximum pool size.
     * @param keepAliveTime   The keep alive time.
     * @param unit            The time unit.
     * @param workQueue       The work queue.
     * @param handler         The rejected execution handler.
     * @return The newly created thread pool executor.
     * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, RejectedExecutionHandler)
     * @since 0.10.0
     */
    ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler);

    /**
     * Creates a new thread pool executor.
     * <p>
     * The resulting executor will be tracked by the error tracker.
     *
     * @param corePoolSize    The core pool size.
     * @param maximumPoolSize The maximum pool size.
     * @param keepAliveTime   The keep alive time.
     * @param unit            The time unit.
     * @param workQueue       The work queue.
     * @param threadFactory   The thread factory.
     * @param handler         The rejected execution handler.
     * @return The newly created thread pool executor.
     * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory, RejectedExecutionHandler)
     * @since 0.10.0
     */
    ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler);
}
