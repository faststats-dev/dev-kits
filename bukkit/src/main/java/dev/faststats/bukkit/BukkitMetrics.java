package dev.faststats.bukkit;

import dev.faststats.core.Metrics;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

/**
 * Bukkit metrics implementation.
 *
 * @since 0.1.0
 */
public sealed interface BukkitMetrics extends Metrics permits BukkitMetricsImpl {
    /**
     * Creates a new metrics factory for Bukkit.
     *
     * @return the metrics factory
     * @since 0.1.0
     */
    @Contract(pure = true)
    static Factory factory() {
        return new BukkitMetricsImpl.Factory();
    }

    /**
     * Registers additional exception handlers on Paper-based implementations.
     *
     * @throws IllegalPluginAccessException if the plugin is not yet enabled
     * @apiNote This method may only be called {@link Plugin#onEnable() onEnable()}.
     * @since 0.14.0
     */
    @Override
    void ready() throws IllegalPluginAccessException;

    interface Factory extends Metrics.Factory<Plugin, Factory> {
        @Override
        BukkitMetrics create(Plugin object) throws IllegalStateException;
    }
}
