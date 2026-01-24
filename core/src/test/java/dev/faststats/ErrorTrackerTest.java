package dev.faststats;

import dev.faststats.core.ErrorTracker;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionException;

public class ErrorTrackerTest {
    // todo: add redaction tests
    // todo: add nesting tests
    // todo: add duplicate tests

    @Test
    // todo: fix this mess
    public void testCompile() throws InterruptedException {
        final var tracker = ErrorTracker.contextUnaware();
        tracker.attachErrorContext(null);

        try {
            roundAndRound(10);
        } catch (final Throwable t) {
            tracker.trackError(t);
        }
        try {
            recursiveError();
        } catch (final Throwable t) {
            tracker.trackError("↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ↓→ħſðđſ→ðđ””ſ→ʒðđ↓ʒ”ſðđʒ");
            tracker.trackError(t);
        }
        try {
            aroundAndAround();
        } catch (final Throwable t) {
            tracker.trackError(t);
            return;
        }

        tracker.trackError("Test error");
        final var nestedError = new RuntimeException("Nested error");
        final var error = new RuntimeException(null, nestedError);
        tracker.trackError(error);

        tracker.trackError("hello my name is david");
        tracker.trackError("/home/MyName/Documents/MyFile.txt");
        tracker.trackError("C:\\Users\\MyName\\AppData\\Local\\Temp");
        tracker.trackError("/Users/MyName/AppData/Local/Temp");
        tracker.trackError("my ipv4 address is 215.223.110.131");
        tracker.trackError("my ipv6 address is f833:be65:65da:975b:4896:88f7:6964:44c0");

        final var deepAsyncError = new RuntimeException("deep async error");

        final var thisIsANiceError = new Thread(() -> {
            final var nestedAsyncError = new RuntimeException("nested async error", deepAsyncError);
            throw new CompletionException("async error", nestedAsyncError);
        });
        thisIsANiceError.start();
        thisIsANiceError.join(Duration.ofSeconds(1));

        Thread.sleep(1000);

        tracker.trackError("Test error");
    }

    public void recursiveError() throws StackOverflowError {
        goRoundAndRound();
    }

    public void goRoundAndRound() throws StackOverflowError {
        andRoundAndRound();
    }

    public void andRoundAndRound() throws StackOverflowError {
        goRoundAndRound();
    }

    public void aroundAndAround() throws StackOverflowError {
        aroundAndAround();
    }

    public void roundAndRound(final int i) throws RuntimeException {
        if (i <= 0) throw new RuntimeException("out of stack");
        roundAndRound(i - 1);
    }
}
