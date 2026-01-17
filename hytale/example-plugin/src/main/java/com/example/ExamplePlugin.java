package com.example;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.faststats.core.Metrics;
import dev.faststats.core.chart.Chart;
import dev.faststats.hytale.HytaleMetrics;

import java.net.URI;

public class ExamplePlugin extends JavaPlugin {
    private final Metrics metrics = HytaleMetrics.factory()
            .url(URI.create("https://metrics.example.com/v1/collect")) // For self-hosted metrics servers only

            // Custom example charts
            // For this to work you have to create a corresponding data source in your project settings first
            .addChart(Chart.number("example_chart", () -> 42))
            .addChart(Chart.string("example_string", () -> "Hello, World!"))
            .addChart(Chart.bool("example_boolean", () -> true))
            .addChart(Chart.stringArray("example_string_array", () -> new String[]{"Option 1", "Option 2"}))
            .addChart(Chart.numberArray("example_number_array", () -> new Number[]{1, 2, 3}))
            .addChart(Chart.booleanArray("example_boolean_array", () -> new Boolean[]{true, false}))

            .debug(true) // Enable debug mode for development and testing

            .token("YOUR_TOKEN_HERE") // required -> token can be found in the settings of your project
            .create(this);

    public ExamplePlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void shutdown() {
        metrics.shutdown();
    }
}
