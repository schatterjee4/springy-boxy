package hm.binkley.man.aggregate;

import hm.binkley.man.command.CheckedTestFailureCommand;
import hm.binkley.man.event.CheckedTestFailureEvent;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.util.UUID;

@ToString
public class CheckedTestFailureAggregate
        extends AbstractAnnotatedAggregateRoot<UUID> {
    @AggregateIdentifier
    private UUID id;

    public CheckedTestFailureAggregate() {}

    @CommandHandler
    public CheckedTestFailureAggregate(
            final CheckedTestFailureCommand command) {
        apply(new CheckedTestFailureEvent(command.getId()));
    }

    @EventHandler
    public void on(final CheckedTestFailureEvent event,
            final EventMessage<CheckedTestFailureEvent> message) {
        id = event.id;
    }
}
