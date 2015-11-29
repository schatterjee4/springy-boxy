package hm.binkley.man.aggregate;

import hm.binkley.man.command.UncheckedTestFailureCommand;
import hm.binkley.man.event.UncheckedTestFailureEvent;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.util.UUID;

@ToString
public class UncheckedTestFailureAggregate
        extends AbstractAnnotatedAggregateRoot<UUID> {
    @AggregateIdentifier
    private UUID id;

    public UncheckedTestFailureAggregate() {}

    @CommandHandler
    public UncheckedTestFailureAggregate(
            final UncheckedTestFailureCommand command) {
        apply(new UncheckedTestFailureEvent(command.getId()));
    }

    @EventHandler
    public void on(final UncheckedTestFailureEvent event,
            final EventMessage<UncheckedTestFailureEvent> message) {
        id = event.id;
    }
}
