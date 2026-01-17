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
    private HytaleMetricsImpl(SimpleMetrics.Factory<?> factory, HytaleLogger logger, Path config) throws IllegalStateException {
        super(factory, config);
        this.logger = logger;

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        charts.addProperty("server_version", HytaleServer.get().getServerName());
        charts.addProperty("player_count", Universe.get().getPlayerCount());
        charts.addProperty("server_type", "Hytale");
    }

    @Override
    protected void printError(String message, @Nullable Throwable throwable) {
        logger.atSevere().log(message, throwable);
    }

    @Override
    protected void printInfo(String message) {
        logger.atInfo().log(message);
    }

    @Override
    protected void printWarning(String message) {
        logger.atWarning().log(message);
    }

    static final class Factory extends SimpleMetrics.Factory<JavaPlugin> {
        @Override
        public Metrics create(JavaPlugin plugin) throws IllegalStateException {
            var mods = plugin.getDataDirectory().toAbsolutePath().getParent();
            var config = mods.resolve("faststats").resolve("config.properties");
            return new HytaleMetricsImpl(this, plugin.getLogger(), config);
        }
    }
}
