package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationStartedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

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
    private Supplier<List<AxonExecution>> executionsFactory;
    private List<AxonExecution> executions;

    @Before
    public void setUp() {
        executions = executionsFactory.get();
    }

    @Test
    public void shouldFireOnApplicationStarted() {
        final String id = randomUUID().toString();
        commandGateway.send(StartApplicationCommand.builder().
                id(id).
                build());

        executions.stream().
                filter(execution -> eventClass(execution).
                        equals(ApplicationStartedListener.class)).
                map(ApplicationStartedListenerIT::eventFirstArg).
                map(ApplicationStartedEvent::getId).
                forEach(eventId -> assertThat(eventId).
                        isEqualTo(id));
    }

    private static Class eventClass(final AxonExecution execution) {
        return execution.handler.getSignature().getDeclaringType();
    }

    private static ApplicationStartedEvent eventFirstArg(
            final AxonExecution execution) {
        return (ApplicationStartedEvent) execution.handler.getArgs()[0];
    }
}
