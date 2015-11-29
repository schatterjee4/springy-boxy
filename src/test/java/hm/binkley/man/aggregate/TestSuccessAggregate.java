package hm.binkley.man.aggregate;

import hm.binkley.man.command.TestSuccessCommand;
import hm.binkley.man.event.TestSuccessEvent;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.util.UUID;

@ToString
public class TestSuccessAggregate
        extends AbstractAnnotatedAggregateRoot<UUID> {
    @AggregateIdentifier
    private UUID id;

    public TestSuccessAggregate() {}

    @CommandHandler
    public TestSuccessAggregate(final TestSuccessCommand command) {
        apply(new TestSuccessEvent(command.getId()));
    }

    @EventHandler
    public void on(final TestSuccessEvent event,
            final EventMessage<TestSuccessEvent> message) {
        id = event.id;
    }
}
