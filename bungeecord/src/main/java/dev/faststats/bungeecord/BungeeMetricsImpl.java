package dev.faststats.bungeecord;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class BungeeMetricsImpl extends SimpleMetrics implements BungeeMetrics {
    private final Logger logger;
    private final ProxyServer server;
    private final Plugin plugin;

    @Async.Schedule
    @Contract(mutates = "io")
    private BungeeMetricsImpl(SimpleMetrics.Factory<?> factory, Plugin plugin, Path config) throws IOException, IllegalStateException {
        super(factory, config);

        this.logger = plugin.getLogger();
        this.server = plugin.getProxy();
        this.plugin = plugin;

        startSubmitting();
    }

    @Async.Schedule
    private void startSubmitting() {
        startSubmitting(30, 30 * 60, TimeUnit.SECONDS);
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
    protected void error(String message, @Nullable Throwable throwable) {
        if (!isDebug()) return;
        var msg = "[" + BungeeMetricsImpl.class.getName() + "]: " + message;
        logger.log(Level.SEVERE, msg, throwable);
    }

    @Override
    protected void warn(String message) {
        if (!isDebug()) return;
        var msg = "[" + BungeeMetricsImpl.class.getName() + "]: " + message;
        logger.log(Level.WARNING, msg);
    }

    @Override
    protected void info(String message) {
        if (!isDebug()) return;
        var msg = "[" + BungeeMetricsImpl.class.getName() + "]: " + message;
        logger.log(Level.INFO, msg);
    }

    static final class Factory extends SimpleMetrics.Factory<Plugin> {
        @Override
        public Metrics create(Plugin plugin) throws IOException, IllegalStateException {
            var dataFolder = plugin.getProxy().getPluginsFolder().toPath().resolve("faststats");
            var config = dataFolder.resolve("config.properties");
            return new BungeeMetricsImpl(this, plugin, config);
        }
    }
}
