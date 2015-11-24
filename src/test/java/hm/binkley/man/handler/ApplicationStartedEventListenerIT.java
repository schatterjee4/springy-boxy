package hm.binkley.man.handler;

import hm.binkley.Main;
import hm.binkley.man.command.StartApplicationCommand;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class ApplicationStartedEventListenerIT {
    @Inject
    private ApplicationStartedEventListener listener;
    @Inject
    private CommandBus commandBus;

    @Test
    public void shouldCaptureEvents() {
        commandBus.dispatch(
                new GenericCommandMessage<>(StartApplicationCommand.builder().
                        id(randomUUID().toString()).
                        build()));
        assertThat(listener.getEvents()).hasSize(1);
    }
}
