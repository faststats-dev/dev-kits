package dev.faststats;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class BukkitMetricsTest {
    @Test
    public void testCreateData() throws IOException {
        var mock = new MockMetrics(UUID.randomUUID(), "bba4a14eac38779007a6fda4814381ab", true);
        var data = mock.createData();
        var bytes = data.toString().getBytes(StandardCharsets.UTF_8);
        try (var byteOutput = new ByteArrayOutputStream();
             var output = new GZIPOutputStream(byteOutput)) {
            output.write(bytes);
            output.finish();
            var compressed = byteOutput.toByteArray();
            mock.printInfo(new String(compressed, StandardCharsets.UTF_8) + " (" + compressed.length + " bytes)");
            mock.printInfo(new String(bytes, StandardCharsets.UTF_8) + " (" + bytes.length + " bytes)");
        }
    }
}
