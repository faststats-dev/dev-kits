import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.faststats.errors {
    exports dev.faststats.errors.concurrent;
    exports dev.faststats.errors;

    requires com.google.gson;
    
    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}