package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationEndedEvent;
import lombok.ToString;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ToString
public class ApplicationEndedListener {
    public EventMessage<ApplicationEndedEvent> message;

    @EventHandler
    public void on(final ApplicationEndedEvent event,
            final EventMessage<ApplicationEndedEvent> message) {
        this.message = message;
    }
}
