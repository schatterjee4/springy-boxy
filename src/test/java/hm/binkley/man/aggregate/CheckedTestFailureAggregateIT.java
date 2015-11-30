package hm.binkley.man.aggregate;

import hm.binkley.Main;
import hm.binkley.man.TestConfiguration;
import hm.binkley.man.command.CheckedTestFailureCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.UUID.randomUUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Main.class, TestConfiguration.class})
@DirtiesContext
public class CheckedTestFailureAggregateIT {
    @Rule
    public final SystemOutRule sout = new SystemOutRule().
            enableLog().
            muteForSuccessfulTests();

    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ConsistentRecords records;

    @Test
    public void shouldTrackCheckedFailedFlow() {
        commandGateway.send(new CheckedTestFailureCommand(randomUUID()));

        records.assertConsistency(1);
    }
}
