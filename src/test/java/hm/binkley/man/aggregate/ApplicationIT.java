package hm.binkley.man.aggregate;

import hm.binkley.Main;
import hm.binkley.man.TestConfiguration;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.ExecutionRecord;
import hm.binkley.man.audit.UnitOfWorkRecord;
import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
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

import static java.lang.System.out;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Main.class, TestConfiguration.class})
@DirtiesContext
public class ApplicationIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<ExecutionRecord> executionRecords;
    @Inject
    private ArrayList<AuditRecord> auditRecords;
    @Inject
    private ArrayList<UnitOfWorkRecord> unitOfWorkRecords;

    @Test
    public void shouldTrackFlow() {
        commandGateway.send(StartApplicationCommand.builder().
                id(randomUUID()).
                build());
        commandGateway.send(EndApplicationCommand.builder().
                id(randomUUID()).
                build());

        unitOfWorkRecords.forEach(out::println);
        auditRecords.forEach(out::println);
        executionRecords.forEach(out::println);

        assertThat(executionRecords).isNotEmpty();
        assertThat(auditRecords).isNotEmpty();
        assertThat(unitOfWorkRecords).isNotEmpty();
        final Set<Object> cids = concat(concat(executions(), records()),
                workUnits()).
                collect(toSet());
        assertThat(cids).hasSize(2);
    }

    private Stream<String> executions() {
        return executionRecords.stream().
                map(ExecutionRecord::getCommandIdentifier);
    }

    private Stream<String> records() {
        return auditRecords.stream().
                map(AuditRecord::getCommandIdentifier);
    }

    private Stream<String> workUnits() {
        return unitOfWorkRecords.stream().
                flatMap(UnitOfWorkRecord::getCommandIdentifiers);
    }
}
