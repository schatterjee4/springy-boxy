package hm.binkley.man.handler;

import hm.binkley.man.event.UncheckedTestFailureEvent;
import lombok.ToString;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@ToString
public class UncheckedTestFailureListener {
    @EventHandler
    public void on(@Nonnull final UncheckedTestFailureEvent event,
            final EventMessage<UncheckedTestFailureEvent> message) {
        throw new UncheckedTestFailureException();
    }

    public static final class UncheckedTestFailureException
            extends RuntimeException {}
}
