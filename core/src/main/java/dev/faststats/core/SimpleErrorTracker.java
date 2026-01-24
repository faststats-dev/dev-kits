package dev.faststats.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.faststats.core.concurrent.TrackingBase;
import dev.faststats.core.concurrent.TrackingExecutors;
import dev.faststats.core.concurrent.TrackingThreadFactory;
import dev.faststats.core.concurrent.TrackingThreadPoolExecutor;
import org.jspecify.annotations.Nullable;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

final class SimpleErrorTracker implements ErrorTracker {
    private final int messageLength = Math.min(1000, Integer.getInteger("faststats.message-length", 500));
    private final int stackTraceLength = Math.min(500, Integer.getInteger("faststats.stack-trace-length", 300));
    private final int stackTraceLimit = Math.min(50, Integer.getInteger("faststats.stack-trace-limit", 15));

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
        final var compile = compile(error, null);
        final var hashed = hash(compile);
        if (collected.compute(hashed, (k, v) -> {
            return v == null ? 1 : v + 1;
        }) > 1) return;
        reports.put(hashed, compile);
    }

    private String hash(final JsonObject report) {
        final var hash = MurmurHash3.hash(report.toString());
        return Long.toHexString(hash[0]) + Long.toHexString(hash[1]);
    }

    // todo: cleanup this absolute mess
    private JsonObject compile(final Throwable error, @Nullable final List<String> suppress) {
        final var elements = error.getStackTrace();
        final var stack = collapseStackTrace(elements);
        final var list = new ArrayList<>(stack);
        if (suppress != null) list.removeAll(suppress);

        final var traces = Math.min(list.size(), stackTraceLimit);

        final var report = new JsonObject();
        final var stacktrace = new JsonArray(traces);

        for (var i = 0; i < traces; i++) {
            final var string = list.get(i);
            if (string.length() <= stackTraceLength) stacktrace.add(string);
            else stacktrace.add(string.substring(0, stackTraceLength) + "...");
        }
        if (traces > 0 && traces < list.size()) {
            stacktrace.add("and " + (list.size() - traces) + " more...");
        } else {
            final var i = elements.length - list.size();
            if (i > 0) stacktrace.add("Omitted " + i + " duplicate stack frame" + (i == 1 ? "" : "s"));
        }

        report.addProperty("error", error.getClass().getName());
        var message = error.getMessage();
        if (message != null) {
            if (message.length() > messageLength) message = message.substring(0, messageLength) + "...";
            report.addProperty("message", anonymize(message));
        }
        if (!stacktrace.isEmpty()) {
            report.add("stack", stacktrace);
        }
        if (error.getCause() != null) {
            final var toSuppress = new ArrayList<>(stack);
            if (suppress != null) toSuppress.addAll(suppress);
            report.add("cause", compile(error.getCause(), toSuppress));
        }

        return report;
    }

    public static List<String> collapseStackTrace(final StackTraceElement[] trace) {
        final var lines = Arrays.stream(trace)
                .map(StackTraceElement::toString)
                .toList();

        return collapseRepeatingPattern(lines);
    }

    public static List<String> collapseRepeatingPattern(final List<String> lines) {
        // First, collapse consecutive duplicate lines
        final var deduplicated = collapseConsecutiveDuplicates(lines);

        final var n = deduplicated.size();

        for (var cycleLen = 1; cycleLen <= n / 2; cycleLen++) {
            var isPattern = true;
            var repetitions = 0;

            for (var i = 0; i < n; i++) {
                if (!deduplicated.get(i).equals(deduplicated.get(i % cycleLen))) {
                    isPattern = false;
                    break;
                }
                if (i > 0 && i % cycleLen == 0) {
                    repetitions++;
                }
            }

            if (isPattern && repetitions >= 2) {
                return deduplicated.subList(0, cycleLen);
            }
        }

        return deduplicated;
    }

    private static List<String> collapseConsecutiveDuplicates(final List<String> lines) {
        if (lines.isEmpty()) return lines;

        final var result = new ArrayList<String>();
        String previous = null;

        for (final var line : lines) {
            if (!line.equals(previous)) {
                result.add(line);
                previous = line;
            }
        }

        return result;
    }

    private static final String IPV4_PATTERN =
            "\\b(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\b";
    private static final String IPV6_PATTERN =
            "(?i)\\b([0-9a-f]{1,4}:){7}[0-9a-f]{1,4}\\b|" +                          // Full form
                    "(?i)\\b([0-9a-f]{1,4}:){1,7}:\\b|" +                                     // Trailing ::
                    "(?i)\\b([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}\\b|" +                        // :: in middle (1 group after)
                    "(?i)\\b([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}\\b|" +                 // :: in middle (2 groups after)
                    "(?i)\\b([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}\\b|" +                 // :: in middle (3 groups after)
                    "(?i)\\b([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}\\b|" +                 // :: in middle (4 groups after)
                    "(?i)\\b([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}\\b|" +                 // :: in middle (5 groups after)
                    "(?i)\\b[0-9a-f]{1,4}:(:[0-9a-f]{1,4}){1,6}\\b|" +                        // :: in middle (6 groups after)
                    "(?i)\\b:(:[0-9a-f]{1,4}){1,7}\\b|" +                                     // Leading ::
                    "(?i)\\b::([0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4}\\b|" +                       // :: at start
                    "(?i)\\b::\\b";                                                           // Just ::
    private static final String USER_HOME_PATH_PATTERN =
            "(/home/)[^/\\s]+" +                                                      // Linux: /home/username
                    "|(/Users/)[^/\\s]+" +                                                    // macOS: /Users/username
                    "|((?i)[A-Z]:\\\\Users\\\\)[^\\\\\\s]+";                                  // Windows: A-Z:\\Users\\username

    private String anonymize(String message) {
        message = message.replaceAll(IPV4_PATTERN, "[IP hidden]");
        message = message.replaceAll(IPV6_PATTERN, "[IP hidden]");
        message = message.replaceAll(USER_HOME_PATH_PATTERN, "$1$2$3[username hidden]");
        final var username = System.getProperty("user.name");
        if (username != null) message = message.replace(username, "[username hidden]");
        return message;
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

    @Override
    public synchronized void attachErrorContext(@Nullable final ClassLoader loader) throws IllegalStateException {
        if (originalHandler != null) throw new IllegalStateException("Error context already attached");
        originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            final var handler = originalHandler;
            if (handler != null) handler.uncaughtException(thread, error);
            try {
                if (loader != null && !isSameLoader(loader, error)) return;
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

    private boolean isSameLoader(final ClassLoader loader, final Throwable error) {
        final var stackTrace = error.getStackTrace();
        if (stackTrace == null || stackTrace.length == 0) {
            return false;
        }

        final var firstNonLibraryIndex = findFirstNonLibraryFrameIndex(stackTrace);
        if (firstNonLibraryIndex == -1) {
            return false;
        }

        final var framesToCheck = Math.min(5, stackTrace.length - firstNonLibraryIndex);

        for (var i = 0; i < framesToCheck; i++) {
            final var frame = stackTrace[firstNonLibraryIndex + i];
            if (isLibraryClass(frame.getClassName())) {
                continue;
            }
            if (!isFromLoader(frame, loader)) {
                return false;
            }
        }

        return true;
    }

    private int findFirstNonLibraryFrameIndex(final StackTraceElement[] stackTrace) {
        for (var i = 0; i < stackTrace.length; i++) {
            if (!isLibraryClass(stackTrace[i].getClassName())) {
                return i;
            }
        }
        return -1;
    }

    private boolean isLibraryClass(final String className) {
        return className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.")
                || className.startsWith("jdk.");
    }

    private boolean isFromLoader(final StackTraceElement frame, final ClassLoader loader) {
        try {
            final var clazz = Class.forName(frame.getClassName(), false, loader);
            return isSameClassLoader(clazz.getClassLoader(), loader);
        } catch (final ClassNotFoundException e) {
            return false;
        } catch (final Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
    }

    private boolean isSameClassLoader(final ClassLoader classLoader, final ClassLoader loader) {
        if (classLoader == loader) return true;

        var current = classLoader;
        while (current != null && current != loader) {
            current = current.getParent();
        }
        return loader == current;
    }
}
