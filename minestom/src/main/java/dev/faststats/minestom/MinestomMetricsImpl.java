package dev.faststats.minestom;

import com.google.gson.JsonObject;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

final class MinestomMetricsImpl extends SimpleMetrics implements MinestomMetrics {
    private final Logger logger = LoggerFactory.getLogger(MinestomMetricsImpl.class);

    @Async.Schedule
    @Contract(mutates = "io")
    private MinestomMetricsImpl(SimpleMetrics.Factory<?> factory, Path config) throws IOException, IllegalStateException {
        super(factory, config);

        startSubmitting();
    }

    @Async.Schedule
    private void startSubmitting() {
        startSubmitting(30, 30 * 60, TimeUnit.SECONDS);
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
        charts.addProperty("minecraft_version", MinecraftServer.VERSION_NAME);
        charts.addProperty("online_mode", !(MinecraftServer.process().auth() instanceof Auth.Offline));
        charts.addProperty("player_count", MinecraftServer.getConnectionManager().getOnlinePlayerCount());
        charts.addProperty("server_type", "Minestom");
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

    static final class Factory extends SimpleMetrics.Factory<MinecraftServer> {
        @Override
        public Metrics create(MinecraftServer server) throws IOException, IllegalStateException {
            var config = Path.of("faststats", "config.properties");
            return new MinestomMetricsImpl(this, config);
        }
    }
}
