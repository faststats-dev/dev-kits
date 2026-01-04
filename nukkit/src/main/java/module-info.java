import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.faststats.nukkit {
    exports dev.faststats.nukkit;

    requires com.google.gson;
    requires dev.faststats.core;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}