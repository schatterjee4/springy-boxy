package hm.binkley.man.aggregate;

import hm.binkley.Main;
import hm.binkley.man.TestConfiguration;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.HandlerExecutionRecord;
import hm.binkley.man.command.TestSuccessCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Main.class, TestConfiguration.class})
@DirtiesContext
public class TestSuccessAggregateIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<HandlerExecutionRecord> executions;
    @Inject
    private ArrayList<AuditRecord> records;

    @Test
    public void shouldTrackSuccessfulFlow() {
        commandGateway.send(new TestSuccessCommand(randomUUID()));

        assertThat(executions).isNotEmpty();
        assertThat(records).isNotEmpty();
        final Set<Object> cids = concat(executions(), records()).
                collect(toSet());
        assertThat(cids).hasSize(1);
    }

    private Stream<String> executions() {
        return executions.stream().
                map(HandlerExecutionRecord::getCommandIdentifier);
    }

    private Stream<String> records() {
        return records.stream().
                map(AuditRecord::getCommandIdentifier);
    }
}