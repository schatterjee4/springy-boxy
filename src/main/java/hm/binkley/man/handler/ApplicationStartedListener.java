package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationStartedEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedListener {
    public EventMessage<ApplicationStartedEvent> message;

    @EventHandler
    public void on(final ApplicationStartedEvent event,
            final EventMessage<ApplicationStartedEvent> message) {
        this.message = message;
    }
}
