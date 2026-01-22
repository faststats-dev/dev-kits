package dev.faststats.nukkit;

import cn.nukkit.plugin.PluginBase;
import dev.faststats.core.Metrics;
import org.jetbrains.annotations.Contract;

/**
 * Nukkit metrics implementation.
 *
 * @since 0.8.0
 */
public sealed interface NukkitMetrics extends Metrics permits NukkitMetricsImpl {
    /**
     * Creates a new metrics factory for Nukkit.
     *
     * @return the metrics factory
     * @since 0.8.0
     */
    @Contract(pure = true)
    static Factory factory() {
        return new NukkitMetricsImpl.Factory();
    }

    interface Factory extends Metrics.Factory<PluginBase, Factory> {
    }
}
