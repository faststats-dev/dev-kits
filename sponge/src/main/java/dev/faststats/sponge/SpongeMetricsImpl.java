package dev.faststats.sponge;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;

final class SpongeMetricsImpl extends SimpleMetrics implements SpongeMetrics {
    public static final String COMMENT = """
             FastStats (https://faststats.dev) collects anonymous usage statistics for plugin developers.
            # This helps developers understand how their projects are used in the real world.
            #
            # No IP addresses, player data, or personal information is collected.
            # The server ID below is randomly generated and can be regenerated at any time.
            #
            # Enabling metrics has no noticeable performance impact.
            # Enabling metrics is recommended, you can do so in the Sponge config.
            #
            # If you suspect a plugin is collecting personal data or bypassing the Sponge config,
            # please report it at: https://faststats.dev/abuse
            #
            # For more information, visit: https://faststats.dev/info
            """;

    private final Logger logger;
    private final PluginContainer plugin;

    @Async.Schedule
    @Contract(mutates = "io")
    private SpongeMetricsImpl(
            SimpleMetrics.Factory<?> factory,
            Logger logger,
            PluginContainer plugin,
            Path config
    ) throws IllegalStateException {
        super(factory, SimpleMetrics.Config.read(config, COMMENT, true, Sponge.metricsConfigManager()
                .effectiveCollectionState(plugin).asBoolean()));

        this.logger = logger;
        this.plugin = plugin;

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        charts.addProperty("online_mode", Sponge.server().isOnlineModeEnabled());
        charts.addProperty("player_count", Sponge.server().onlinePlayers().size());
        charts.addProperty("plugin_version", plugin.metadata().version().toString());
        charts.addProperty("minecraft_version", Sponge.platform().minecraftVersion().name());
        charts.addProperty("server_type", Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().id());
    }

    @Override
    protected void printError(String message, @Nullable Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    protected void printInfo(String message) {
        logger.info(message);
    }

    @Override
    protected void printWarning(String message) {
        logger.warn(message);
    }

    static class Factory extends SimpleMetrics.Factory<PluginContainer> {
        protected final Logger logger;
        protected final Path dataDirectory;

        public Factory(Logger logger, @ConfigDir(sharedRoot = true) Path dataDirectory) {
            this.logger = logger;
            this.dataDirectory = dataDirectory;
        }

        @Override
        public Metrics create(PluginContainer plugin) throws IllegalStateException, IllegalArgumentException {
            var faststats = dataDirectory.resolveSibling("faststats");
            return new SpongeMetricsImpl(this, logger, plugin, faststats.resolve("config.properties"));
        }
    }
}
