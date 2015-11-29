package hm.binkley.man.handler;

import hm.binkley.man.event.CheckedTestFailureEvent;
import lombok.ToString;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@ToString
public class CheckedTestFailureListener {
    @EventHandler
    public void on(@Nonnull final CheckedTestFailureEvent event,
            final EventMessage<CheckedTestFailureEvent> message)
            throws CheckedTestFailureException {
        throw new CheckedTestFailureException();
    }

    public static final class CheckedTestFailureException
            extends Exception {}
}
