package dev.faststats.bukkit;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

final class BukkitMetricsImpl extends SimpleMetrics implements BukkitMetrics {
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

        startSubmitting();
    }

    private boolean checkOnlineMode() {
        return tryOrEmpty(() -> server.getServerConfig().isProxyOnlineMode())
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
        System.out.println("#appendDefaultData pluginVersion:");
        printInfo("#appendDefaultData pluginVersion:");

        var pluginVersion = tryOrEmpty(() -> plugin.getPluginMeta().getVersion())
                .orElseGet(() -> plugin.getDescription().getVersion());

        System.out.println(pluginVersion);
        printInfo(pluginVersion);

        System.out.println("#appendDefaultData minecraftVersion:");
        printInfo("#appendDefaultData minecraftVersion:");


        System.out.println(server.getBukkitVersion());
        System.out.println(server.getVersion());
        printInfo(server.getBukkitVersion());
        printInfo(server.getVersion());

        var minecraftVersion = tryOrEmpty(server::getMinecraftVersion)
                .orElseGet(() -> {
                    try {
                        System.out.println(server.getBukkitVersion());
                        printInfo(server.getBukkitVersion());
                        return server.getBukkitVersion().split("-", 2)[0];
                    } catch (Exception e) {
                        System.err.println("Failed to get Minecraft version");
                        e.printStackTrace(System.err);
                        printError("Failed to get Minecraft version", e);
                        return server.getVersion();
                    }
                });
        //.or(() -> tryOrEmpty(() -> server.getBukkitVersion().split("-", 2)[0]))
        //.orElseGet(() -> server.getVersion());

        System.out.println(minecraftVersion);
        printInfo(minecraftVersion);

        charts.addProperty("minecraft_version", minecraftVersion);
        charts.addProperty("online_mode", checkOnlineMode());
        charts.addProperty("player_count", server.getOnlinePlayers().size());
        charts.addProperty("plugin_version", pluginVersion);
        charts.addProperty("server_type", server.getName());

        System.out.println("#appendDefaultData done");
        printInfo("#appendDefaultData done");
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
            return Optional.empty();
        }
    }

    static final class Factory extends SimpleMetrics.Factory<Plugin> {
        @Override
        public Metrics create(Plugin plugin) throws IllegalStateException {
            var dataFolder = getPluginsFolder(plugin).resolve("faststats");
            var config = dataFolder.resolve("config.properties");
            return new BukkitMetricsImpl(this, plugin, config);
        }

        private static Path getPluginsFolder(Plugin plugin) {
            try {
                return plugin.getServer().getPluginsFolder().toPath();
            } catch (NoSuchMethodError e) {
                return plugin.getDataFolder().getParentFile().toPath();
            }
        }
    }
}
