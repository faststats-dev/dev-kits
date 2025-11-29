package dev.faststats.bungee;

import dev.faststats.core.Metrics;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Contract;

/**
 * BungeeCord metrics implementation.
 *
 * @since 0.1.0
 */
public sealed interface BungeeMetrics extends Metrics permits BungeeMetricsImpl {
    /**
     * Creates a new metrics factory for BungeeCord.
     *
     * @return the metrics factory
     * @since 0.1.0
     */
    @Contract(pure = true)
    static Factory<Plugin> factory() {
        return new BungeeMetricsImpl.Factory();
    }
}
