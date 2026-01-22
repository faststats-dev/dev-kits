package dev.faststats.fabric;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

final class FabricMetricsImpl extends SimpleMetrics implements FabricMetrics {
    private final Logger logger = LoggerFactory.getLogger("FastStats");
    private final ModContainer mod;

    private @Nullable MinecraftServer server;

    @Async.Schedule
    @Contract(mutates = "io")
    private FabricMetricsImpl(Factory factory, ModContainer mod, Path config) throws IllegalStateException {
        super(factory, config);

        this.mod = mod;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            startSubmitting();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> shutdown());
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        assert server != null : "Server not initialized";
        charts.addProperty("minecraft_version", server.getServerVersion());
        charts.addProperty("online_mode", server.usesAuthentication());
        charts.addProperty("player_count", server.getPlayerCount());
        charts.addProperty("plugin_version", mod.getMetadata().getVersion().getFriendlyString());
        charts.addProperty("server_type", "Fabric");
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

    private <T> Optional<T> tryOrEmpty(Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (NoSuchMethodError | Exception e) {
            return Optional.empty();
        }
    }

    static final class Factory extends SimpleMetrics.Factory<String, FabricMetrics.Factory> implements FabricMetrics.Factory {
        @Override
        public Metrics create(String modId) throws IllegalStateException, IllegalArgumentException {
            var fabric = FabricLoader.getInstance();
            var mod = fabric.getModContainer(modId).orElseThrow(() -> {
                return new IllegalArgumentException("Mod not found: " + modId);
            });

            var dataFolder = fabric.getConfigDir().resolve("faststats");
            var config = dataFolder.resolve("config.properties");

            return new FabricMetricsImpl(this, mod, config);
        }
    }
}
