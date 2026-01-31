package dev.faststats.bukkit;

import com.google.gson.JsonObject;
import dev.faststats.core.SimpleMetrics;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

final class BukkitMetricsImpl extends SimpleMetrics implements BukkitMetrics {
    private final Plugin plugin;

    private final String pluginVersion;
    private final String minecraftVersion;
    private final String serverType;

    @Async.Schedule
    @Contract(mutates = "io")
    @SuppressWarnings({"deprecation", "Convert2MethodRef"})
    private BukkitMetricsImpl(final Factory factory, final Plugin plugin, final Path config) throws IllegalStateException {
        super(factory, config);

        this.plugin = plugin;
        var server = plugin.getServer();

        this.pluginVersion = tryOrEmpty(() -> plugin.getPluginMeta().getVersion())
                .orElseGet(() -> plugin.getDescription().getVersion());
        this.minecraftVersion = tryOrEmpty(() -> server.getMinecraftVersion())
                .or(() -> tryOrEmpty(() -> server.getBukkitVersion().split("-", 2)[0]))
                .orElseGet(() -> server.getVersion().split("\\(MC: |\\)", 3)[1]);
        this.serverType = server.getName();

        startSubmitting();
    }

    Plugin plugin() {
        return plugin;
    }

    private boolean checkOnlineMode() {
        var server = plugin.getServer();
        return tryOrEmpty(() -> server.getServerConfig().isProxyOnlineMode())
                .or(() -> tryOrEmpty(this::isProxyOnlineMode))
                .orElseGet(server::getOnlineMode);
    }

    @SuppressWarnings("removal")
    private boolean isProxyOnlineMode() {
        var server = plugin.getServer();
        final var proxies = server.spigot().getPaperConfig().getConfigurationSection("proxies");
        if (proxies == null) return false;

        if (proxies.getBoolean("velocity.enabled") && proxies.getBoolean("velocity.online-mode")) return true;

        final var settings = server.spigot().getSpigotConfig().getConfigurationSection("settings");
        if (settings == null) return false;

        return settings.getBoolean("bungeecord") && proxies.getBoolean("bungee-cord.online-mode");
    }

    @Override
    protected void appendDefaultData(final JsonObject charts) {
        charts.addProperty("minecraft_version", minecraftVersion);
        charts.addProperty("online_mode", checkOnlineMode());
        charts.addProperty("player_count", getPlayerCount());
        charts.addProperty("plugin_version", pluginVersion);
        charts.addProperty("server_type", serverType);
    }

    private int getPlayerCount() {
        try {
            return plugin.getServer().getOnlinePlayers().size();
        } catch (final Throwable t) {
            error("Failed to get player count", t);
            return 0;
        }
    }

    @Override
    protected void printError(final String message, @Nullable final Throwable throwable) {
        plugin.getLogger().log(Level.SEVERE, message, throwable);
    }

    @Override
    protected void printInfo(final String message) {
        plugin.getLogger().info(message);
    }

    @Override
    protected void printWarning(final String message) {
        plugin.getLogger().warning(message);
    }

    @Override
    public void ready() {
        if (getErrorTracker().isPresent()) try {
            Class.forName("com.destroystokyo.paper.event.server.ServerExceptionEvent");
            plugin.getServer().getPluginManager().registerEvents(new PaperEventListener(this), plugin);
        } catch (final ClassNotFoundException ignored) {
        }
    }

    private <T> Optional<T> tryOrEmpty(final Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (final NoSuchMethodError | Exception e) {
            return Optional.empty();
        }
    }

    static final class Factory extends SimpleMetrics.Factory<Plugin, BukkitMetrics.Factory> implements BukkitMetrics.Factory {
        @Override
        public BukkitMetrics create(final Plugin plugin) throws IllegalStateException {
            final var dataFolder = getPluginsFolder(plugin).resolve("faststats");
            final var config = dataFolder.resolve("config.properties");
            return new BukkitMetricsImpl(this, plugin, config);
        }

        private static Path getPluginsFolder(final Plugin plugin) {
            try {
                return plugin.getServer().getPluginsFolder().toPath();
            } catch (final NoSuchMethodError e) {
                return plugin.getDataFolder().getParentFile().toPath();
            }
        }
    }
}
