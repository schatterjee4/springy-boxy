package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationStartedEvent;
import lombok.ToString;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ToString
public class ApplicationStartedListener {
    public EventMessage<ApplicationStartedEvent> message;

    @EventHandler
    public void on(final ApplicationStartedEvent event,
            final EventMessage<ApplicationStartedEvent> message) {
        this.message = message;
    }
}
