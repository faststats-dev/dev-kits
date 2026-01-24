package dev.faststats.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.faststats.core.concurrent.TrackingBase;
import dev.faststats.core.concurrent.TrackingExecutors;
import dev.faststats.core.concurrent.TrackingThreadFactory;
import dev.faststats.core.concurrent.TrackingThreadPoolExecutor;
import org.jspecify.annotations.Nullable;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

final class SimpleErrorTracker implements ErrorTracker {
    private final Map<String, Integer> collected = new ConcurrentHashMap<>();
    private final Map<String, JsonObject> reports = new ConcurrentHashMap<>();

    private final TrackingBase base = new SimpleTrackingBase(this);
    private final TrackingExecutors executors = new SimpleTrackingExecutors(this);
    private final TrackingThreadFactory threadFactory = new SimpleTrackingThreadFactory(this);
    private final TrackingThreadPoolExecutor threadPoolExecutor = new SimpleTrackingThreadPoolExecutor(this);

    private volatile @Nullable BiConsumer<@Nullable ClassLoader, Throwable> errorEvent = null;
    private volatile @Nullable UncaughtExceptionHandler originalHandler = null;

    @Override
    public void trackError(final String message) {
        trackError(new RuntimeException(message));
    }

    @Override
    public void trackError(final Throwable error) {
        final var compiled = ErrorHelper.compile(error, null);
        final var hashed = MurmurHash3.hash(compiled);
        if (collected.compute(hashed, (k, v) -> {
            return v == null ? 1 : v + 1;
        }) > 1) return;
        reports.put(hashed, compiled);
    }

    public JsonArray getData() {
        final var report = new JsonArray(reports.size());

        reports.forEach((hash, object) -> {
            final var copy = object.deepCopy();
            copy.addProperty("hash", hash);
            final var count = collected.getOrDefault(hash, 1);
            if (count > 1) copy.addProperty("count", count);
            report.add(copy);
        });

        collected.forEach((hash, count) -> {
            if (count <= 0 || reports.containsKey(hash)) return;
            final var entry = new JsonObject();

            entry.addProperty("hash", hash);
            if (count > 1) entry.addProperty("count", count);

            report.add(entry);
        });

        return report;
    }

    public void clear() {
        collected.replaceAll((k, v) -> 0);
        reports.clear();
    }

    public boolean needsFlushing() {
        if (!reports.isEmpty()) return false;
        for (final var value : collected.values()) {
            if (value > 0) return false;
        }
        return true;
        
    }

    @Override
    public synchronized void attachErrorContext(@Nullable final ClassLoader loader) throws IllegalStateException {
        if (originalHandler != null) throw new IllegalStateException("Error context already attached");
        originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            final var handler = originalHandler;
            if (handler != null) handler.uncaughtException(thread, error);
            try {
                if (loader != null && !ErrorHelper.isSameLoader(loader, error)) return;
                final var event = errorEvent;
                if (event != null) event.accept(loader, error);
                trackError(error);
            } catch (final Throwable t) {
                trackError(t);
            }
        });
    }

    @Override
    public synchronized void detachErrorContext() {
        if (originalHandler == null) return;
        Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        originalHandler = null;
    }

    @Override
    public synchronized boolean isContextAttached() {
        return originalHandler != null;
    }

    @Override
    public synchronized void setContextErrorHandler(@Nullable final BiConsumer<@Nullable ClassLoader, Throwable> errorEvent) {
        this.errorEvent = errorEvent;
    }

    @Override
    public synchronized Optional<BiConsumer<@Nullable ClassLoader, Throwable>> getContextErrorHandler() {
        return Optional.ofNullable(errorEvent);
    }

    @Override
    public TrackingBase base() {
        return base;
    }

    @Override
    public TrackingExecutors executors() {
        return executors;
    }

    @Override
    public TrackingThreadFactory threadFactory() {
        return threadFactory;
    }

    @Override
    public TrackingThreadPoolExecutor threadPoolExecutor() {
        return threadPoolExecutor;
    }
}
