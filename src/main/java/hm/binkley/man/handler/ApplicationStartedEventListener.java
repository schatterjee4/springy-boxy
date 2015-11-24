package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationStartedEvent;
import lombok.Getter;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationStartedEventListener {
    @Getter
    private final List<ApplicationStartedEvent> events = new ArrayList<>();

    @EventHandler
    public void on(final ApplicationStartedEvent event) {
        events.add(event);
    }
}
