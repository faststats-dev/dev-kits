import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.faststats.bungeecord {
    exports dev.faststats.bungeecord;

    requires bungeecord.api;
    requires com.google.gson;
    requires dev.faststats.core;
    requires java.logging;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}