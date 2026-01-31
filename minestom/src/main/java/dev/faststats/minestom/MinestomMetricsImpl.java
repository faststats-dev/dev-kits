package dev.faststats.minestom;

import com.google.gson.JsonObject;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.SimpleMetrics;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

final class MinestomMetricsImpl extends SimpleMetrics implements MinestomMetrics {
    private final Logger logger = LoggerFactory.getLogger(MinestomMetricsImpl.class);

    @Async.Schedule
    @Contract(mutates = "io")
    private MinestomMetricsImpl(final Factory factory, final Path config) throws IllegalStateException {
        super(factory, config);

        startSubmitting();
    }

    @Override
    protected void appendDefaultData(final JsonObject charts) {
        charts.addProperty("minecraft_version", MinecraftServer.VERSION_NAME);
        charts.addProperty("online_mode", !(MinecraftServer.process().auth() instanceof Auth.Offline));
        charts.addProperty("player_count", MinecraftServer.getConnectionManager().getOnlinePlayerCount());
        charts.addProperty("server_type", "Minestom");
    }

    @Override
    protected void printError(final String message, @Nullable final Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    protected void printInfo(final String message) {
        logger.info(message);
    }

    @Override
    protected void printWarning(final String message) {
        logger.warn(message);
    }

    @Override
    public void ready() {
        getErrorTracker().ifPresent(this::registerExceptionHandler);
    }

    private void registerExceptionHandler(ErrorTracker errorTracker) {
        var handler = MinecraftServer.getExceptionManager().getExceptionHandler();
        MinecraftServer.getExceptionManager().setExceptionHandler(error -> {
            handler.handleException(error);
            if (!ErrorTracker.isSameLoader(getClass().getClassLoader(), error)) return;
            errorTracker.trackError(error);
        });
    }

    static final class Factory extends SimpleMetrics.Factory<MinecraftServer, MinestomMetrics.Factory> implements MinestomMetrics.Factory {
        @Override
        public Metrics create(final MinecraftServer server) throws IllegalStateException {
            final var config = Path.of("faststats", "config.properties");
            return new MinestomMetricsImpl(this, config);
        }
    }
}
