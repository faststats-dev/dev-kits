package dev.faststats.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Logger;
import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

final class NukkitMetricsImpl extends SimpleMetrics implements NukkitMetrics {
    private final Logger logger;
    private final Server server;
    private final PluginBase plugin;

    @Async.Schedule
    @Contract(mutates = "io")
    private NukkitMetricsImpl(SimpleMetrics.Factory<?> factory, PluginBase plugin, Path config) throws IllegalStateException {
        super(factory, config);

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
        this.plugin = plugin;

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        charts.addProperty("minecraft_version", server.getVersion());
        charts.addProperty("online_mode", server.xboxAuth);
        charts.addProperty("player_count", server.getOnlinePlayersCount());
        charts.addProperty("plugin_version", plugin.getDescription().getVersion());
        charts.addProperty("server_type", server.getName());
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
        logger.warning(message);
    }

    private <T> Optional<T> tryOrEmpty(Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (NoSuchMethodError | Exception e) {
            return Optional.empty();
        }
    }

    static final class Factory extends SimpleMetrics.Factory<PluginBase> {
        @Override
        public Metrics create(PluginBase plugin) throws IllegalStateException {
            var dataFolder = Path.of(plugin.getServer().getPluginPath(), "faststats");
            var config = dataFolder.resolve("config.properties");
            return new NukkitMetricsImpl(this, plugin, config);
        }
    }
}
