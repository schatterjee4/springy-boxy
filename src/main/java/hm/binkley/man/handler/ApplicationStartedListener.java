package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationStartedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedListener {
    @EventHandler
    public void on(final ApplicationStartedEvent event) {}
}
