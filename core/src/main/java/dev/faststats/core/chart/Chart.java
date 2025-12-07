package dev.faststats.core.chart;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * A chart.
 *
 * @param <T> the chart data type
 * @since 0.1.0
 */
public interface Chart<T> {
    /**
     * Get the chart id.
     *
     * @return the chart id
     * @since 0.1.0
     */
    @ChartId
    @Contract(pure = true)
    String getId();

    /**
     * Compute the chart data.
     *
     * @return an optional containing the chart data
     * @throws Exception if unable to compute the chart data
     * @implSpec The implementation must be thread-safe and pure (i.e. not modify any shared state).
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<T> compute() throws Exception;

    /**
     * Get the chart data as a JSON element.
     *
     * @return an optional containing the chart data as {@link JsonElement}
     * @throws Exception if unable to get the chart data
     * @implSpec The implementation must call {@link #compute()} to get the chart data
     * and follow the same thread-safety and pureness requirements.
     * @see #compute()
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<JsonElement> getData() throws Exception;

    /**
     * Create a string array chart.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the string array chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.5.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<String[]> stringArray(@ChartId String id, Callable<String @Nullable []> callable) throws IllegalArgumentException {
        return new ArrayChart<>(id, callable);
    }

    /**
     * Create a boolean array chart.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the boolean array chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.5.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<Boolean[]> booleanArray(@ChartId String id, Callable<Boolean @Nullable []> callable) throws IllegalArgumentException {
        return new ArrayChart<>(id, callable);
    }

    /**
     * Create a number array chart.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the number array chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.5.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<Number[]> numberArray(@ChartId String id, Callable<Number @Nullable []> callable) throws IllegalArgumentException {
        return new ArrayChart<>(id, callable);
    }

    /**
     * Create a chart for a boolean value.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the boolean chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.1.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<Boolean> bool(@ChartId String id, Callable<@Nullable Boolean> callable) throws IllegalArgumentException {
        return new SingleValueChart<>(id, callable);
    }

    /**
     * Create a chart for a string value.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the string chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.1.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<String> string(@ChartId String id, Callable<@Nullable String> callable) throws IllegalArgumentException {
        return new SingleValueChart<>(id, callable);
    }

    /**
     * Create a chart for a number value.
     *
     * @param id       the chart id
     * @param callable the chart data callable
     * @return the number chart
     * @throws IllegalArgumentException if the chart id is invalid
     * @apiNote The callable must be thread-safe and pure (i.e. not modify any shared state).
     * @see #compute()
     * @since 0.1.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Chart<Number> number(@ChartId String id, Callable<@Nullable Number> callable) throws IllegalArgumentException {
        return new SingleValueChart<>(id, callable);
    }
}
