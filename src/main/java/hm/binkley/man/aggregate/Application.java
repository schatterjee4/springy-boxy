package hm.binkley.man.aggregate;

import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import hm.binkley.man.event.ApplicationStartedEvent;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

/**
 * {@code Application} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
public class Application
        extends AbstractAnnotatedAggregateRoot {
    @AggregateIdentifier
    private String id;

    public Application() {}

    @CommandHandler
    public Application(final StartApplicationCommand command) {
        apply(ApplicationStartedEvent.builder().
                id(command.getId()).
                build());
    }

    @EventHandler
    public void on(final ApplicationStartedEvent event) {
        id = event.getId();
    }

    @CommandHandler
    public void end(final EndApplicationCommand command) {
        apply(ApplicationEndedEvent.builder().
                id(command.getId()).
                build());
    }
}
