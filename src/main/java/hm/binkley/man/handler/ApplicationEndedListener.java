package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationEndedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEndedListener {
    @EventHandler
    public void on(final ApplicationEndedEvent event) {}
}
