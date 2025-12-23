package dev.faststats.core;

import com.google.gson.JsonObject;
import dev.faststats.core.chart.Chart;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.VisibleForTesting;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

public abstract class SimpleMetrics implements Metrics {
    protected static final String ONBOARDING_MESSAGE = """
            This plugin uses FastStats to collect anonymous usage statistics.
            No personal or identifying information is ever collected.
            To opt out, set 'enabled=false' in the metrics configuration file.
            Learn more at: https://faststats.dev/info""";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private @Nullable ScheduledExecutorService executor = null;

    private final Set<Chart<?>> charts;
    private final Config config;
    private final @Token String token;
    private final URI url;
    private final boolean debug;

    @Contract(mutates = "io")
    @SuppressWarnings("PatternValidation")
    protected SimpleMetrics(SimpleMetrics.Factory<?> factory, Path config) throws IllegalStateException {
        if (factory.token == null) throw new IllegalStateException("Token must be specified");

        this.charts = Set.copyOf(factory.charts);
        this.config = new Config(config);
        this.debug = factory.debug;
        this.token = factory.token;
        this.url = factory.url;
    }

    @VisibleForTesting
    protected SimpleMetrics(Config config, Set<Chart<?>> charts, @Token String token, URI url, boolean debug) {
        if (!token.matches(Token.PATTERN)) {
            throw new IllegalArgumentException("Invalid token '" + token + "', must match '" + Token.PATTERN + "'");
        }

        this.charts = Set.copyOf(charts);
        this.config = config;
        this.debug = debug;
        this.token = token;
        this.url = url;
    }

    @Async.Schedule
    @MustBeInvokedByOverriders
    protected void startSubmitting(int initialDelay, int period, TimeUnit unit) {
        if (config.firstRun) {
            for (var s : ONBOARDING_MESSAGE.split("\n")) printInfo(s);
        }

        if (!config.enabled()) {
            warn("Metrics disabled, not starting submission");
            return;
        }

        if (isSubmitting()) {
            warn("Metrics already submitting, not starting again");
            return;
        }

        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            var thread = new Thread(runnable, "metrics-submitter");
            thread.setDaemon(true);
            return thread;
        });

        info("Starting metrics submission");
        executor.scheduleAtFixedRate(this::submitData, initialDelay, period, unit);
    }

    protected boolean isSubmitting() {
        return executor != null && !executor.isShutdown();
    }

    protected void submitData() {
        var data = createData().toString();
        var bytes = data.getBytes(StandardCharsets.UTF_8);
        try (var byteOutput = new ByteArrayOutputStream();
             var output = new GZIPOutputStream(byteOutput)) {
            output.write(bytes);
            output.finish();
            var compressed = byteOutput.toByteArray();
            var request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofByteArray(compressed))
                    .header("Content-Encoding", "gzip")
                    .header("Content-Type", "application/octet-stream")
                    .header("Authorization", "Bearer " + getToken())
                    .header("User-Agent", "FastStats Metrics")
                    .timeout(Duration.ofSeconds(3))
                    .uri(url)
                    .build();

            info("Sending metrics to: " + url);
            info("Uncompressed data: " + data);
            info("Compressed size: " + compressed.length + " bytes");

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            var statusCode = response.statusCode();
            var body = response.body();

            if (statusCode >= 200 && statusCode < 300) {
                info("Metrics submitted with status code: " + statusCode + " (" + body + ")");
            } else if (statusCode >= 300 && statusCode < 400) {
                warn("Received redirect response from metrics server: " + statusCode + " (" + body + ")");
            } else if (statusCode >= 400 && statusCode < 500) {
                error("Submitted invalid request to metrics server: " + statusCode + " (" + body + ")", null);
            } else if (statusCode >= 500 && statusCode < 600) {
                error("Received server error response from metrics server: " + statusCode + " (" + body + ")", null);
            } else {
                warn("Received unexpected response from metrics server: " + statusCode + " (" + body + ")");
            }

        } catch (HttpConnectTimeoutException e) {
            error("Metrics submission timed out after 3 seconds: " + url, null);
        } catch (ConnectException e) {
            error("Failed to connect to metrics server: " + url, null);
        } catch (Exception e) {
            error("Failed to submit metrics", e);
        }
    }

    protected JsonObject createData() {
        var data = new JsonObject();
        var charts = new JsonObject();

        charts.addProperty("java_version", System.getProperty("java.version"));
        charts.addProperty("os_arch", System.getProperty("os.arch"));
        charts.addProperty("os_name", System.getProperty("os.name"));
        charts.addProperty("os_version", System.getProperty("os.version"));
        charts.addProperty("core_count", Runtime.getRuntime().availableProcessors());

        this.charts.forEach(chart -> {
            try {
                chart.getData().ifPresent(chartData -> charts.add(chart.getId(), chartData));
            } catch (Exception e) {
                error("Failed to build chart data: " + chart.getId(), e);
            }
        });

        appendDefaultData(charts);

        data.addProperty("server_id", config.serverId().toString());
        data.add("data", charts);
        return data;
    }

    @Override
    public @Token String getToken() {
        return token;
    }

    @Override
    public Metrics.Config getConfig() {
        return config;
    }

    protected boolean isDebug() {
        return debug || config.debug();
    }

    @Contract(mutates = "param1")
    protected abstract void appendDefaultData(JsonObject charts);

    protected void error(String message, @Nullable Throwable throwable) {
        if (isDebug()) printError("[" + getClass().getName() + "]: " + message, throwable);
    }

    protected void warn(String message) {
        if (isDebug()) printWarning("[" + getClass().getName() + "]: " + message);
    }

    protected void info(String message) {
        if (isDebug()) printInfo("[" + getClass().getName() + "]: " + message);
    }

    protected abstract void printError(String message, @Nullable Throwable throwable);

    protected abstract void printInfo(String message);

    protected abstract void printWarning(String message);

    @Override
    public void shutdown() {
        info("Shutting down metrics submission");
        if (executor == null) return;
        executor.shutdown();
        executor = null;
    }

    public abstract static class Factory<T> implements Metrics.Factory<T> {
        private final Set<Chart<?>> charts = new HashSet<>(0);
        private URI url = URI.create("https://metrics.faststats.dev/v1/collect");
        private @Nullable String token;
        private boolean debug = false;

        @Override
        public Metrics.Factory<T> addChart(Chart<?> chart) throws IllegalArgumentException {
            if (!charts.add(chart)) throw new IllegalArgumentException("Chart already added: " + chart.getId());
            return this;
        }

        @Override
        public Metrics.Factory<T> debug(boolean enabled) {
            this.debug = enabled;
            return this;
        }

        @Override
        public Metrics.Factory<T> token(@Token String token) throws IllegalArgumentException {
            if (!token.matches(Token.PATTERN)) {
                throw new IllegalArgumentException("Invalid token '" + token + "', must match '" + Token.PATTERN + "'");
            }
            this.token = token;
            return this;
        }

        @Override
        public Metrics.Factory<T> url(URI url) {
            this.url = url;
            return this;
        }
    }

    protected static final class Config implements Metrics.Config {
        private final UUID serverId;
        private final boolean debug;
        private final boolean enabled;
        private final boolean firstRun;

        @Contract(mutates = "io")
        protected Config(Path file) {
            var properties = readOrEmpty(file);
            this.firstRun = properties.isEmpty();
            var saveConfig = new AtomicBoolean(this.firstRun);

            this.serverId = properties.map(object -> object.getProperty("serverId")).map(string -> {
                try {
                    var trimmed = string.trim();
                    var corrected = trimmed.length() > 36 ? trimmed.substring(0, 36) : trimmed;
                    if (!corrected.equals(string)) saveConfig.set(true);
                    return UUID.fromString(corrected);
                } catch (IllegalArgumentException e) {
                    saveConfig.set(true);
                    return UUID.randomUUID();
                }
            }).orElseGet(UUID::randomUUID);

            this.enabled = properties.map(object -> object.getProperty("enabled")).map(Boolean::parseBoolean).orElse(true);
            this.debug = properties.map(object -> object.getProperty("debug")).map(Boolean::parseBoolean).orElse(false);

            if (saveConfig.get()) try {
                save(file, serverId, enabled, debug);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save metrics config", e);
            }
        }

        @VisibleForTesting
        public Config(UUID serverId, boolean enabled, boolean debug) {
            this.serverId = serverId;
            this.enabled = enabled;
            this.debug = debug;
            this.firstRun = false;
        }

        @Override
        public UUID serverId() {
            return serverId;
        }

        @Override
        public boolean enabled() {
            return enabled;
        }

        @Override
        public boolean debug() {
            return debug;
        }

        private static Optional<Properties> readOrEmpty(Path file) {
            if (!Files.isRegularFile(file)) return Optional.empty();
            try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                var properties = new Properties();
                properties.load(reader);
                return Optional.of(properties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read metrics config", e);
            }
        }

        private static void save(Path file, UUID serverId, boolean enabled, boolean debug) throws IOException {
            Files.createDirectories(file.getParent());
            try (var out = Files.newOutputStream(file);
                 var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                var properties = new Properties();

                properties.setProperty("serverId", serverId.toString());
                properties.setProperty("enabled", Boolean.toString(enabled));
                properties.setProperty("debug", Boolean.toString(debug));

                var comment = """
                         FastStats (https://faststats.dev) gathers basic information for plugin developers,
                        # such as the number of users and total player count.
                        # Keeping metrics enabled is recommended, but you can disable them if you prefer.
                        # Enabling metrics does not affect performance,
                        # and all data sent to FastStats is completely anonymous.
                        
                        # If you suspect a plugin is collecting personal data or bypassing the "enabled" option,
                        # please report it to the FastStats team (https://faststats.dev/abuse).
                        
                        # For more information, visit https://faststats.dev/info
                        """;
                properties.store(writer, comment);
            }
        }
    }
}
