package dev.faststats.core.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Callable;

final class SingleValueMetric<T> extends SimpleMetric<T> {
    public SingleValueMetric(@SourceId final String id, final Callable<@Nullable T> callable) throws IllegalArgumentException {
        super(id, callable);
    }

    @Override
    public Optional<JsonElement> getData() throws Exception {
        return compute().map(data -> switch (data) {
            case final Boolean bool -> new JsonPrimitive(bool);
            case final Number number -> new JsonPrimitive(number);
            default -> new JsonPrimitive(data.toString());
        });
    }
}
