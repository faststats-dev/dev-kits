package dev.faststats.hytale;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.faststats.core.Metrics;
import org.jetbrains.annotations.Contract;

/**
 * Hytale metrics implementation.
 *
 * @since 0.1.0
 */
public sealed interface HytaleMetrics extends Metrics permits HytaleMetricsImpl {
    /**
     * Creates a new metrics factory for Hytale.
     *
     * @return the metrics factory
     * @since 0.1.0
     */
    @Contract(pure = true)
    static Metrics.Factory<JavaPlugin> factory() {
        return new HytaleMetricsImpl.Factory();
    }
}
