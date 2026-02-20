package dev.faststats.hytale;

import com.google.gson.JsonObject;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.Universe;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

final class HytaleMetricsImpl extends SimpleMetrics implements HytaleMetrics {
    private final HytaleLogger logger;

    @Async.Schedule
    @Contract(mutates = "io")
    private HytaleMetricsImpl(final Factory factory, final HytaleLogger logger, final Path config) throws IllegalStateException {
        super(factory, config);
        this.logger = logger;

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(final JsonObject metrics) {
        metrics.addProperty("server_version", HytaleServer.get().getServerName());
        metrics.addProperty("player_count", Universe.get().getPlayerCount());
        metrics.addProperty("server_type", "Hytale");
    }

    @Override
    protected void printError(final String message, @Nullable final Throwable throwable) {
        logger.atSevere().log(message, throwable);
    }

    @Override
    protected void printInfo(final String message) {
        logger.atInfo().log(message);
    }

    @Override
    protected void printWarning(final String message) {
        logger.atWarning().log(message);
    }

    static final class Factory extends SimpleMetrics.Factory<JavaPlugin, HytaleMetrics.Factory> implements HytaleMetrics.Factory {
        @Override
        public Metrics create(final JavaPlugin plugin) throws IllegalStateException {
            final var mods = plugin.getDataDirectory().toAbsolutePath().getParent();
            final var config = mods.resolve("faststats").resolve("config.properties");
            return new HytaleMetricsImpl(this, plugin.getLogger(), config);
        }
    }
}
