package dev.faststats.bungee;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

final class BungeeMetricsImpl extends SimpleMetrics implements BungeeMetrics {
    private final Logger logger;
    private final ProxyServer server;
    private final Plugin plugin;

    @Async.Schedule
    @Contract(mutates = "io")
    private BungeeMetricsImpl(Factory factory, Plugin plugin, Path config) throws IllegalStateException {
        super(factory, config);

        this.logger = plugin.getLogger();
        this.server = plugin.getProxy();
        this.plugin = plugin;

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        charts.addProperty("online_mode", server.getConfig().isOnlineMode());
        charts.addProperty("player_count", server.getOnlineCount());
        charts.addProperty("plugin_version", plugin.getDescription().getVersion());
        charts.addProperty("proxy_version", server.getVersion());
        charts.addProperty("server_type", server.getName());
    }

    @Override
    protected void printError(String message, @Nullable Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    protected void printInfo(String message) {
        logger.info(message);
    }

    @Override
    protected void printWarning(String message) {
        logger.warning(message);
    }

    static final class Factory extends SimpleMetrics.Factory<Plugin, BungeeMetrics.Factory> implements BungeeMetrics.Factory {
        @Override
        public Metrics create(Plugin plugin) throws IllegalStateException {
            var dataFolder = plugin.getProxy().getPluginsFolder().toPath().resolve("faststats");
            var config = dataFolder.resolve("config.properties");
            return new BungeeMetricsImpl(this, plugin, config);
        }
    }
}
