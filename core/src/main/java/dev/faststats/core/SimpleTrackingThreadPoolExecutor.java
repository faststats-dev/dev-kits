package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingThreadPoolExecutor;
import org.jetbrains.annotations.Range;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class SimpleTrackingThreadPoolExecutor implements TrackingThreadPoolExecutor {
    private final ErrorTracker tracker;

    public SimpleTrackingThreadPoolExecutor(final ErrorTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) final int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, tracker.threadFactory()) {
            @Override
            protected void afterExecute(final Runnable r, final Throwable t) {
                super.afterExecute(r, t);
                tracker.trackError(t);
            }
        };
    }

    @Override
    public ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) final int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory) {
            @Override
            protected void afterExecute(final Runnable r, final Throwable t) {
                super.afterExecute(r, t);
                tracker.trackError(t);
            }
        };
    }

    @Override
    public ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) final int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler) {
            @Override
            protected void afterExecute(final Runnable r, final Throwable t) {
                super.afterExecute(r, t);
                tracker.trackError(t);
            }
        };
    }

    @Override
    public ThreadPoolExecutor create(@Range(from = 0, to = Integer.MAX_VALUE) final int corePoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final int maximumPoolSize, @Range(from = 0, to = Integer.MAX_VALUE) final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler) {
            @Override
            protected void afterExecute(final Runnable r, final Throwable t) {
                super.afterExecute(r, t);
                tracker.trackError(t);
            }
        };
    }
}
