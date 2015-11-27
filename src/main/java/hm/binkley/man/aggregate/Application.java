package hm.binkley.man.aggregate;

import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import hm.binkley.man.event.ApplicationStartedEvent;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.unitofwork.UnitOfWork;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.UUID;

@Configurable()
@ToString
public class Application
        extends AbstractAnnotatedAggregateRoot<UUID> {
    @AggregateIdentifier
    private UUID id;

    public Application() {}

    @CommandHandler
    public Application(final StartApplicationCommand command,
            final UnitOfWork unitOfWork) {
        System.out.println("Application.Application");
        apply(ApplicationStartedEvent.builder().
                id(command.getId()).
                build());
    }

    @EventHandler
    public void on(final ApplicationStartedEvent event) {
        System.out.println("Application.on");
        id = event.getId();
    }

    @CommandHandler
    public void end(final EndApplicationCommand command,
            final UnitOfWork unitOfWork) {
        System.out.println("Application.end");
        apply(ApplicationEndedEvent.builder().
                id(command.getId()).
                build());
    }
}
