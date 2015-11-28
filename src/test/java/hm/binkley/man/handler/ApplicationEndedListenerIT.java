package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.AuditRecord;
import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.domain.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {Main.class, TestConfiguration.class})
@DirtiesContext
public class ApplicationEndedListenerIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<AxonExecution> executions;
    @Inject
    private ArrayList<AuditRecord> records;

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
        assertThat(records).isNotEmpty();
        executions.stream().
                filter(execution -> eventClass(execution).
                        equals(ApplicationEndedListener.class)).
                map(ApplicationEndedListenerIT::eventOf).
                map(ApplicationEndedEvent::getId).
                forEach(eventId -> assertThat(eventId).
                        isEqualTo(id));

        final Set<Object> cids = executions.stream().
                map(AxonExecution::asMessage).
                map(Message::getMetaData).
                map(md -> md.getOrDefault("correlation-identifier", "??")).
                collect(toSet());
        records.stream().
                map(r -> r.command).
                map(Message::getMetaData).
                map(md -> md.getOrDefault("correlation-identifier", "??")).
                collect(toCollection(() -> cids));
        assertThat(cids).hasSize(2);
    }

    private static Class eventClass(final AxonExecution execution) {
        return execution.handler.getSignature().getDeclaringType();
    }

    private static ApplicationEndedEvent eventOf(
            final AxonExecution execution) {
        return execution.<ApplicationEndedEvent>asEvent().
                orElseThrow(IllegalStateException::new);
    }
}
