package hm.binkley.man;

import hm.binkley.Main;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.handler.ApplicationStartedEventListener;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventstore.supporting.VolatileEventStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class ApplicationIT {
    @Inject
    private ApplicationStartedEventListener listener;
    @Inject
    private CommandBus commandBus;
    @Inject
    private VolatileEventStore eventStore;

    @Test
    public void shouldCaptureEvents() {
        final String id = randomUUID().toString();
        commandBus
                .dispatch(asCommandMessage(StartApplicationCommand.builder().
                        id(id).
                        build()));
        assertThat(listener.getEvents()).hasSize(1);

        final AtomicBoolean visited = new AtomicBoolean(false);
        eventStore.visitEvents(domainEvent -> {
            visited.set(true);
            assertThat(domainEvent.getAggregateIdentifier()).isEqualTo(id);
        });
        assertThat(visited.get()).isTrue();
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public VolatileEventStore eventStore() {
            return new VolatileEventStore();
        }
    }
}
