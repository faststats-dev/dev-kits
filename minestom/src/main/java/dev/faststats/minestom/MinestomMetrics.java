package dev.faststats.minestom;

import dev.faststats.core.Metrics;
import net.minestom.server.Auth;
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
    static Factory factory() {
        return new MinestomMetricsImpl.Factory();
    }

    /**
     * Registers additional exception handlers.
     *
     * @apiNote This method may only be called after {@link MinecraftServer#init(Auth)}.
     * @since 0.14.0
     */
    @Override
    void ready();

    interface Factory extends Metrics.Factory<MinecraftServer, Factory> {
    }
}
