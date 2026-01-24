package dev.faststats.core;

import dev.faststats.core.concurrent.TrackingExecutors;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

final class SimpleTrackingExecutors implements TrackingExecutors {
    private final ErrorTracker tracker;

    public SimpleTrackingExecutors(final ErrorTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public ExecutorService newFixedThreadPool(final int threads) {
        return tracker.threadPoolExecutor().create(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    public ExecutorService newWorkStealingPool(final int parallelism) {
        return Executors.newWorkStealingPool(parallelism); // todo
    }

    @Override
    public ExecutorService newWorkStealingPool() {
        return Executors.newWorkStealingPool(); // todo
    }

    @Override
    public ExecutorService newFixedThreadPool(final int threads, final ThreadFactory factory) {
        return Executors.newFixedThreadPool(threads, WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newSingleThreadExecutor() {
        return newSingleThreadExecutor(defaultThreadFactory());
    }

    @Override
    public ExecutorService newSingleThreadExecutor(final ThreadFactory factory) {
        return Executors.newSingleThreadExecutor(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newCachedThreadPool() {
        return tracker.threadPoolExecutor().create(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    @Override
    public ExecutorService newCachedThreadPool(final ThreadFactory factory) {
        return Executors.newCachedThreadPool(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(final ThreadFactory factory) {
        return Executors.newSingleThreadExecutor(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        return newThreadPerTaskExecutor(Thread.ofVirtual().factory());
    }

    @Override
    public ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor(); // todo
    }

    @Override
    public ScheduledExecutorService newSingleThreadScheduledExecutor(final ThreadFactory factory) {
        return Executors.newSingleThreadScheduledExecutor(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(final int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize); // todo
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(final int corePoolSize, final ThreadFactory factory) {
        return Executors.newScheduledThreadPool(corePoolSize, WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService unconfigurableExecutorService(final ExecutorService executor) {
        return Executors.unconfigurableExecutorService(executor); // todo
    }

    @Override
    public ScheduledExecutorService unconfigurableScheduledExecutorService(final ScheduledExecutorService executor) {
        return Executors.unconfigurableScheduledExecutorService(executor); // todo
    }

    @Override
    public ThreadFactory defaultThreadFactory() {
        return new SimpleTrackingThreadFactory(tracker);
    }

    @Override
    public <T> Callable<T> callable(final Runnable task, final T result) {
        return Executors.callable(tracker.base().tracked(task), result);
    }

    @Override
    public Callable<Object> callable(final Runnable task) {
        return Executors.callable(tracker.base().tracked(task));
    }

    @Override
    public Callable<Object> callable(final PrivilegedAction<?> action) {
        return Executors.callable(tracker.base().tracked(action));
    }

    @Override
    public Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        return Executors.callable(tracker.base().tracked(action));
    }
}
