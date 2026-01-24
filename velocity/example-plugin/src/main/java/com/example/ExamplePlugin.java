package com.example;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.chart.Chart;
import dev.faststats.velocity.VelocityMetrics;
import org.jspecify.annotations.Nullable;

import java.net.URI;


@Plugin(id = "example", name = "Example Plugin", version = "1.0.0",
        url = "https://example.com", authors = {"Your Name"})
public class ExamplePlugin {
    // context-aware error tracker, automatically tracks errors in the same class loader
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    // context-unaware error tracker, does not automatically track errors
    public static final ErrorTracker CONTEXT_UNAWARE_ERROR_TRACKER = ErrorTracker.contextUnaware();

    private final VelocityMetrics.Factory metricsFactory;
    private @Nullable Metrics metrics = null;

    @Inject
    public ExamplePlugin(final VelocityMetrics.Factory factory) {
        this.metricsFactory = factory;
    }

    @Subscribe
    public void onProxyInitialize(final ProxyInitializeEvent event) {
        this.metrics = metricsFactory
                .url(URI.create("https://metrics.example.com/v1/collect")) // For self-hosted metrics servers only

                // Custom example charts
                // For this to work you have to create a corresponding data source in your project settings first
                .addChart(Chart.number("example_chart", () -> 42))
                .addChart(Chart.string("example_string", () -> "Hello, World!"))
                .addChart(Chart.bool("example_boolean", () -> true))
                .addChart(Chart.stringArray("example_string_array", () -> new String[]{"Option 1", "Option 2"}))
                .addChart(Chart.numberArray("example_number_array", () -> new Number[]{1, 2, 3}))
                .addChart(Chart.booleanArray("example_boolean_array", () -> new Boolean[]{true, false}))

                // Attach an error tracker
                // This must be enabled in the project settings
                .errorTracker(ERROR_TRACKER)

                .debug(true) // Enable debug mode for development and testing

                .token("YOUR_TOKEN_HERE") // required -> token can be found in the settings of your project
                .create(this);
    }

    @Subscribe
    public void onProxyStop(final ProxyShutdownEvent event) {
        if (metrics != null) metrics.shutdown();
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
