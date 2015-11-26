package hm.binkley.man.handler;

import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import org.axonframework.eventstore.supporting.VolatileEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * {@code TestConfiguration} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 * @todo Nasty ArrayList because of Spring over-cleverness
 */
@Configuration
public class TestConfiguration {
    private final ArrayList<AxonExecution> executions = new ArrayList<>();

    @Bean
    public ArrayList<AxonExecution> axonExecutions() {
        return executions;
    }

    @Bean
    @Primary
    public Consumer<? super AxonExecution> axonExecutionConsumer() {
        return executions::add;
    }

    @Bean
    public VolatileEventStore eventStore() {
        return new VolatileEventStore();
    }
}
