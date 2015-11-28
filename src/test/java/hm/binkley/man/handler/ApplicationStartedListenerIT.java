package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationStartedEvent;
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
public class ApplicationStartedListenerIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<AxonExecution> executions;

    @Test
    public void shouldFireOnApplicationStarted() {
        final UUID id = randomUUID();
        commandGateway.send(StartApplicationCommand.builder().
                id(id).
                build());

        assertThat(executions).isNotEmpty();
        executions.stream().
                filter(execution -> eventClass(execution).
                        equals(ApplicationStartedListener.class)).
                map(ApplicationStartedListenerIT::eventOf).
                map(ApplicationStartedEvent::getId).
                forEach(eventId -> assertThat(eventId).
                        isEqualTo(id));
    }

    private static Class eventClass(final AxonExecution execution) {
        return execution.handler.getSignature().getDeclaringType();
    }

    private static ApplicationStartedEvent eventOf(
            final AxonExecution execution) {
        return execution.<ApplicationStartedEvent>asEvent().
                orElseThrow(IllegalStateException::new);
    }
}
