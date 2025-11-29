package dev.faststats.minestom;

import dev.faststats.core.Metrics;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.Contract;

/**
 * Minestom metrics implementation.
 *
 * @since 0.1.0
 */
public sealed interface MinestomMetrics extends Metrics permits MinestomMetricsImpl {
    /**
     * Creates a new metrics factory forMinestom.
     *
     * @return the metrics factory
     * @since 0.1.0
     */
    @Contract(pure = true)
    static Metrics.Factory<MinecraftServer> factory() {
        return new MinestomMetricsImpl.Factory();
    }
}
