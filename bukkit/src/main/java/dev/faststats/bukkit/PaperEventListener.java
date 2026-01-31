package dev.faststats.bukkit;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerPluginException;
import dev.faststats.core.ErrorTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

record PaperEventListener(BukkitMetricsImpl metrics) implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerException(final ServerExceptionEvent event) {
        metrics.printError("Received exception", event.getException()); // todo: remove
        if (event.getException() instanceof final ServerPluginException exception) {
            if (!exception.getResponsiblePlugin().equals(metrics.plugin())) return;
            metrics.getErrorTracker().ifPresent(tracker -> tracker.trackError(exception));
        } else if (ErrorTracker.isSameLoader(metrics.plugin().getClass().getClassLoader(), event.getException())) {
            metrics.printError("Received exception from same class loader", event.getException()); // todo: remove
            metrics.getErrorTracker().ifPresent(tracker -> tracker.trackError(event.getException()));
        }
    }
}
