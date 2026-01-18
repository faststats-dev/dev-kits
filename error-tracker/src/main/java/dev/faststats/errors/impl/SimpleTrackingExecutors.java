package dev.faststats.errors.impl;

import dev.faststats.errors.ErrorTracker;
import dev.faststats.errors.concurrent.TrackingExecutors;

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

public final class SimpleTrackingExecutors implements TrackingExecutors {
    private final ErrorTracker tracker;

    public SimpleTrackingExecutors(ErrorTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public ExecutorService newFixedThreadPool(int threads) {
        return tracker.threadPoolExecutor().create(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    public ExecutorService newWorkStealingPool(int parallelism) {
        return Executors.newWorkStealingPool(parallelism); // todo
    }

    @Override
    public ExecutorService newWorkStealingPool() {
        return Executors.newWorkStealingPool(); // todo
    }

    @Override
    public ExecutorService newFixedThreadPool(int threads, ThreadFactory factory) {
        return Executors.newFixedThreadPool(threads, WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newSingleThreadExecutor() {
        return newSingleThreadExecutor(defaultThreadFactory());
    }

    @Override
    public ExecutorService newSingleThreadExecutor(ThreadFactory factory) {
        return Executors.newSingleThreadExecutor(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newCachedThreadPool() {
        return tracker.threadPoolExecutor().create(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    @Override
    public ExecutorService newCachedThreadPool(ThreadFactory factory) {
        return Executors.newCachedThreadPool(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(ThreadFactory factory) {
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
    public ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory factory) {
        return Executors.newSingleThreadScheduledExecutor(WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize); // todo
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory factory) {
        return Executors.newScheduledThreadPool(corePoolSize, WrappedTrackingThreadFactory.wrap(tracker, factory));
    }

    @Override
    public ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        return Executors.unconfigurableExecutorService(executor); // todo
    }

    @Override
    public ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        return Executors.unconfigurableScheduledExecutorService(executor); // todo
    }

    @Override
    public ThreadFactory defaultThreadFactory() {
        return new SimpleTrackingThreadFactory(tracker);
    }

    @Override
    public <T> Callable<T> callable(Runnable task, T result) {
        return Executors.callable(tracker.tracked(task), result);
    }

    @Override
    public Callable<Object> callable(Runnable task) {
        return Executors.callable(tracker.tracked(task));
    }

    @Override
    public Callable<Object> callable(PrivilegedAction<?> action) {
        return Executors.callable(tracker.tracked(action));
    }

    @Override
    public Callable<Object> callable(PrivilegedExceptionAction<?> action) {
        return Executors.callable(tracker.tracked(action));
    }
}
