package com.example;

import com.google.inject.Inject;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.data.Metric;
import dev.faststats.sponge.SpongeMetrics;
import org.jspecify.annotations.Nullable;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.net.URI;


@Plugin("example")
public class ExamplePlugin {
    // context-aware error tracker, automatically tracks errors in the same class loader
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    // context-unaware error tracker, does not automatically track errors
    public static final ErrorTracker CONTEXT_UNAWARE_ERROR_TRACKER = ErrorTracker.contextUnaware();

    private @Inject PluginContainer pluginContainer;
    private @Inject SpongeMetrics.Factory factory;

    private @Nullable Metrics metrics = null;

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {
        this.metrics = factory
                .url(URI.create("https://metrics.example.com/v1/collect")) // For self-hosted metrics servers only

                // Custom example metrics
                // For this to work you have to create a corresponding data source in your project settings first
                .addMetric(Metric.number("example_metric", () -> 42))
                .addMetric(Metric.string("example_string", () -> "Hello, World!"))
                .addMetric(Metric.bool("example_boolean", () -> true))
                .addMetric(Metric.stringArray("example_string_array", () -> new String[]{"Option 1", "Option 2"}))
                .addMetric(Metric.numberArray("example_number_array", () -> new Number[]{1, 2, 3}))
                .addMetric(Metric.booleanArray("example_boolean_array", () -> new Boolean[]{true, false}))

                // Attach an error tracker
                // This must be enabled in the project settings
                .errorTracker(ERROR_TRACKER)

                .debug(true) // Enable debug mode for development and testing

                .token("YOUR_TOKEN_HERE") // required -> token can be found in the settings of your project
                .create(pluginContainer);
    }

    @Listener
    public void onServerStop(final StoppingEngineEvent<Server> event) {
        if (metrics != null) metrics.shutdown(); // safely shut down metrics submission
    }

    public void doSomethingWrong() {
        try {
            // Do something that might throw an error
            throw new RuntimeException("Something went wrong!");
        } catch (final Exception e) {
            CONTEXT_UNAWARE_ERROR_TRACKER.trackError(e);
        }
    }
}
