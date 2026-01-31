package com.example;

import dev.faststats.bungee.BungeeMetrics;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.chart.Chart;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.URI;

public class ExamplePlugin extends Plugin {
    // context-aware error tracker, automatically tracks errors in the same class loader
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    // context-unaware error tracker, does not automatically track errors
    public static final ErrorTracker CONTEXT_UNAWARE_ERROR_TRACKER = ErrorTracker.contextUnaware();

    private final Metrics metrics = BungeeMetrics.factory()
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

    @Override
    public void onDisable() {
        metrics.shutdown(); // safely shut down metrics submission
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
