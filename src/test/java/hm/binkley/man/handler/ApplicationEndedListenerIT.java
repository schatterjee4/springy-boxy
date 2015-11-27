package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.TestConfiguration;
import hm.binkley.man.audit.HandlerExecutionRecord;
import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {Main.class, TestConfiguration.class})
@DirtiesContext
public class ApplicationEndedListenerIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<HandlerExecutionRecord> executions;

    @Test
    public void shouldFireOnApplicationEnded() {
        final UUID id = randomUUID();
        commandGateway.send(StartApplicationCommand.builder().
                id(id).
                build());
        commandGateway.send(EndApplicationCommand.builder().
                id(id).
                build());

        assertThat(executions).isNotEmpty();
        executions.stream().
                filter(execution -> eventClass(execution).
                        equals(ApplicationEndedListener.class)).
                map(ApplicationEndedListenerIT::eventOf).
                map(ApplicationEndedEvent::getId).
                forEach(eventId -> assertThat(eventId).
                        isEqualTo(id));
    }

    private static Class eventClass(final HandlerExecutionRecord execution) {
        return execution.handler.getSignature().getDeclaringType();
    }

    private static ApplicationEndedEvent eventOf(
            final HandlerExecutionRecord execution) {
        return execution.<ApplicationEndedEvent>asEvent().
                orElseThrow(IllegalStateException::new);
    }
}
