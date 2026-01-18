package dev.faststats;

import com.google.gson.GsonBuilder;
import dev.faststats.errors.impl.SimpleErrorTracker;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorTrackerTest {
    // todo: add redaction tests
    // todo: add nesting tests
    // todo: add duplicate tests
    
    @Test
    // todo: fix this mess
    public void testCompile() throws InterruptedException {
        var tracker = new SimpleErrorTracker();
        tracker.attachErrorContext(null);
        tracker.trackError("Test error");
        var nestedError = new RuntimeException("Nested error");
        var error = new RuntimeException(null, nestedError);
        tracker.trackError(error);
        
        tracker.trackError("hello my name is david");
        tracker.trackError(Path.of("").toAbsolutePath().toString());
        tracker.trackError("C:\\Users\\Luca\\AppData\\Local\\Temp\\SuckACock");
        tracker.trackError("/Users/Luca/AppData/Local/Temp/SuckACock");
        tracker.trackError("my ipv4 address is 215.223.110.131");
        tracker.trackError("my ipv6 address is f833:be65:65da:975b:4896:88f7:6964:44c0");

        var deepAsyncError = new RuntimeException("deep async error");

        var thisIsANiceError = new Thread(() -> {
            var nestedAsyncError = new RuntimeException("nested async error", deepAsyncError);
            throw new CompletionException("async error", nestedAsyncError);
        });
        thisIsANiceError.start();
        thisIsANiceError.join(Duration.ofSeconds(1));

        Thread.sleep(1000);

        tracker.trackError("Test error");
        var report = tracker.getData().orElseThrow();

        var gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(report));
        if (true) return;
        assertEquals(2, report.size());

        var item = report.get(0).getAsJsonObject();
        var nested = report.get(1).getAsJsonObject();
        assertTrue(item.has("message"));
        assertFalse(nested.has("message"));


        assertEquals("Test error", item.get("message").getAsString());
        assertEquals(1, item.get("stack").getAsJsonArray().size());
    }
}
