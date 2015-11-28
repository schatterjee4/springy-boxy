package hm.binkley.man.aggregate;

import hm.binkley.Main;
import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.handler.TestConfiguration;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.domain.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;

import static java.lang.System.out;
import static java.util.UUID.randomUUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Main.class, TestConfiguration.class})
public class ApplicationIT {
    @Inject
    private CommandGateway commandGateway;
    @Inject
    private ArrayList<AxonExecution> executions;

    @Test
    public void shouldTrackFlow() {
        commandGateway.send(StartApplicationCommand.builder().
                id(randomUUID()).
                build());

        executions.stream().
                map(AxonExecution::asMessage).
                map(o -> o.map(Message::getMetaData)).
                map(o -> o.map(md -> md.getOrDefault("flow-id", "??"))).
                map(o -> String.format("flow-id = %s", o)).
                forEach(out::println);
    }
}
