package dev.faststats;

import com.google.gson.JsonObject;
import dev.faststats.core.SimpleMetrics;
import dev.faststats.core.Token;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class MockMetrics extends SimpleMetrics {
    public MockMetrics(UUID serverId, @Token String token, boolean debug) {
        super(new SimpleMetrics.Config(serverId, true, debug), Set.of(), token, URI.create("http://localhost:5000/v1/collect"), debug);
    }

    @Override
    protected void printError(String message, @Nullable Throwable throwable) {
        System.err.println(message);
        if (throwable != null) throwable.printStackTrace(System.err);
    }

    @Override
    protected void printInfo(String message) {
        System.out.println(message);
    }

    @Override
    protected void printWarning(String message) {
        System.out.println(message);
    }

    @Override
    public CompletableFuture<Boolean> submitAsync() throws IOException {
        return super.submitAsync();
    }

    @Override
    public JsonObject createData() {
        return super.createData();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
    }
}
