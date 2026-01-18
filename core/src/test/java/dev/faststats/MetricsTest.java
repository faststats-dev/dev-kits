package dev.faststats;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class MetricsTest {
    @Test
    public void testCreateData() throws IOException {
        var mock = new MockMetrics(UUID.randomUUID(), "24f9fc423ed06194065a42d00995c600", true);
        assumeTrue(mock.submitAsync().join(), "For this test to run, the server must be running");
    }
}
