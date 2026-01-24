package dev.faststats;

import com.google.gson.JsonObject;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.SimpleMetrics;
import dev.faststats.core.Token;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class MockMetrics extends SimpleMetrics {
    public MockMetrics(UUID serverId, @Token String token, @Nullable ErrorTracker tracker, boolean debug) {
        super(new Config(serverId, true, debug, true, true, false, false), Set.of(), token, tracker, URI.create("http://localhost:5000/v1/collect"), debug);
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
    public boolean submit() throws IOException {
        return super.submit();
    }

    @Override
    public JsonObject createData() {
        return super.createData();
    }

    @Override
    protected void appendDefaultData(JsonObject charts) {
    }
}
