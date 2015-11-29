package hm.binkley.man.handler;

import hm.binkley.man.event.TestSuccessEvent;
import lombok.ToString;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ToString
public class TestSuccessListener {
    @EventHandler
    public void on(final TestSuccessEvent event,
            final EventMessage<TestSuccessEvent> message) {}
}
