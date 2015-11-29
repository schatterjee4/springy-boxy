package hm.binkley.man.aggregate;

import hm.binkley.Main;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.AxonExecution;
import hm.binkley.man.command.CheckedTestFailureCommand;
import hm.binkley.man.command.TestSuccessCommand;
import hm.binkley.man.command.UncheckedTestFailureCommand;
import hm.binkley.man.TestConfiguration;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Main.class, TestConfiguration.class})
@DirtiesContext(methodMode = AFTER_METHOD)
public class TestAggregateIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<AxonExecution> executions;
    @Inject
    private ArrayList<AuditRecord> records;

    @Before
    public void fixMeCodeSmell() {
        executions.clear();
        records.clear();
    }

    @Test
    public void shouldTrackSuccessfulFlow() {
        final UUID id = randomUUID();
        commandGateway.send(new TestSuccessCommand(id));

        assertThat(executions).isNotEmpty();
        assertThat(records).isNotEmpty();
        final Set<Object> cids = concat(executions(), records()).
                collect(toSet());
        assertThat(cids).hasSize(1);
    }

    @Test
    public void shouldTrackCheckedFailedFlow() {
        final UUID id = randomUUID();
        commandGateway.send(new CheckedTestFailureCommand(id));

        assertThat(executions).isNotEmpty();
        assertThat(records).isNotEmpty();
        final Set<Object> cids = concat(executions(), records()).
                collect(toSet());
        assertThat(cids).hasSize(1);
    }

    @Test
    public void shouldTrackUncheckedFailedFlow() {
        final UUID id = randomUUID();
        commandGateway.send(new UncheckedTestFailureCommand(id));

        assertThat(executions).isNotEmpty();
        assertThat(records).isNotEmpty();
        final Set<Object> cids = concat(executions(), records()).
                collect(toSet());
        assertThat(cids).hasSize(1);
    }

    private Stream<String> executions() {
        return executions.stream().
                map(AxonExecution::getCommandIdentifier);
    }

    private Stream<String> records() {
        return records.stream().
                map(AuditRecord::getCommandIdentifier);
    }
}
