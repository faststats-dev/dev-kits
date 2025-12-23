package dev.faststats.bukkit;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

final class BukkitMetricsImpl extends SimpleMetrics implements BukkitMetrics, Listener {
    private final Logger logger;
    private final Server server;
    private final Plugin plugin;

    @Async.Schedule
    @Contract(mutates = "io")
    private BukkitMetricsImpl(SimpleMetrics.Factory<?> factory, Plugin plugin, Path config) throws IllegalStateException {
        super(factory, config);

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (plugin.isEnabled()) startSubmitting();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onServerLoad(ServerLoadEvent event) {
        startSubmitting();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) shutdown();
    }

    @Async.Schedule
    private void startSubmitting() {
        startSubmitting(0, 30, TimeUnit.MINUTES);
    }

    private boolean checkOnlineMode() {
        return tryOrEmpty(server.getServerConfig()::isProxyOnlineMode)
                .or(() -> tryOrEmpty(this::isProxyOnlineMode))
                .orElseGet(server::getOnlineMode);
    }

    @SuppressWarnings("removal")
    private boolean isProxyOnlineMode() {
        var proxies = server.spigot().getPaperConfig().getConfigurationSection("proxies");
        if (proxies == null) return false;

        if (proxies.getBoolean("velocity.enabled") && proxies.getBoolean("velocity.online-mode")) return true;

        var settings = server.spigot().getSpigotConfig().getConfigurationSection("settings");
        if (settings == null) return false;

        return settings.getBoolean("bungeecord") && proxies.getBoolean("bungee-cord.online-mode");
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void appendDefaultData(JsonObject charts) {
        var pluginVersion = tryOrEmpty(() -> plugin.getPluginMeta().getVersion())
                .orElseGet(() -> plugin.getDescription().getVersion());
        var minecraftVersion = tryOrEmpty(server::getMinecraftVersion)
                .orElseGet(() -> server.getBukkitVersion().split("-", 2)[0]);

        charts.addProperty("minecraft_version", minecraftVersion);
        charts.addProperty("online_mode", checkOnlineMode());
        charts.addProperty("player_count", server.getOnlinePlayers().size());
        charts.addProperty("plugin_version", pluginVersion);
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

    private <T> Optional<T> tryOrEmpty(Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (NoSuchMethodError | Exception e) {
            error("Failed to call supplier", e);
            return Optional.empty();
        }
    }

    static final class Factory extends SimpleMetrics.Factory<Plugin> {
        @Override
        public Metrics create(Plugin plugin) throws IllegalStateException {
            var dataFolder = plugin.getServer().getPluginsFolder().toPath().resolve("faststats");
            var config = dataFolder.resolve("config.properties");
            return new BukkitMetricsImpl(this, plugin, config);
        }
    }
}
