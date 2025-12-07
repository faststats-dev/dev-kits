package dev.faststats.core.chart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Callable;

final class ArrayChart<T> extends SimpleChart<T[]> {
    public ArrayChart(@ChartId String id, Callable<T @Nullable []> callable) throws IllegalArgumentException {
        super(id, callable);
    }

    @Override
    public Optional<JsonElement> getData() throws Exception {
        return compute().map(data -> {
            var elements = new JsonArray(data.length);
            for (var d : data) {
                switch (d) {
                    case Boolean b -> elements.add(b);
                    case Number n -> elements.add(n);
                    default -> elements.add(d.toString());
                }
            }
            return elements;
        });
    }
}
