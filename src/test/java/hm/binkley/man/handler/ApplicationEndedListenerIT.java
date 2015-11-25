package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.aspect.AxonFlowRecorder;
import hm.binkley.man.aspect.AxonFlowRecorder.Execution;
import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import org.axonframework.commandhandling.CommandBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@DirtiesContext
public class ApplicationEndedListenerIT {
    @Inject
    private CommandBus commandBus;
    @Inject
    private AxonFlowRecorder executions;

    @Test
    public void shouldFireOnApplicationEnded() {
        final String id = randomUUID().toString();
        commandBus
                .dispatch(asCommandMessage(StartApplicationCommand.builder().
                        id(id).
                        build()));
        commandBus.dispatch(asCommandMessage(EndApplicationCommand.builder().
                id(id).
                build()));

        assertThat(executions).hasSize(4);
        executions.stream().
                filter(execution -> eventClass(execution).
                        equals(ApplicationEndedListener.class)).
                map(ApplicationEndedListenerIT::eventFirstArg).
                map(ApplicationEndedEvent::getId).
                forEach(eventId -> assertThat(eventId).
                        isEqualTo(id));
    }

    private static Class eventClass(final Execution execution) {
        return execution.handler.getSignature().getDeclaringType();
    }

    private static ApplicationEndedEvent eventFirstArg(
            final Execution execution) {
        return (ApplicationEndedEvent) execution.handler.getArgs()[0];
    }
}
