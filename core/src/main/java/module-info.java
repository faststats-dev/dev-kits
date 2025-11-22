import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.faststats.core {
    exports dev.faststats.core.chart;
    exports dev.faststats.core;

    requires com.google.gson;
    requires java.net.http;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}