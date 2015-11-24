package hm.binkley.man.handler;

import hm.binkley.man.event.ApplicationEndedEvent;
import lombok.Getter;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationEndedEventListener {
    @Getter
    private final List<ApplicationEndedEvent> events = new ArrayList<>();

    @EventHandler
    public void on(final ApplicationEndedEvent event) {
        events.add(event);
    }
}
