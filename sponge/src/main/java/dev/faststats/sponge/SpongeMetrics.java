package dev.faststats.sponge;

import com.google.inject.Inject;
import dev.faststats.core.Metrics;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.config.ConfigDir;

import java.nio.file.Path;

/**
 * Sponge metrics implementation.
 *
 * @since 0.12.0
 */
public sealed interface SpongeMetrics extends Metrics permits SpongeMetricsImpl {
    final class Factory extends SpongeMetricsImpl.Factory {
        /**
         * Creates a new metrics factory for Sponge.
         *
         * @param logger        the logger
         * @param dataDirectory the data directory
         * @apiNote This instance is automatically injected into your plugin.
         * @since 0.12.0
         */
        @Inject
        private Factory(final Logger logger, @ConfigDir(sharedRoot = true) final Path dataDirectory) {
            super(logger, dataDirectory);
        }
    }
}
