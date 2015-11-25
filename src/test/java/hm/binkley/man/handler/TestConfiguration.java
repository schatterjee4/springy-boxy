package hm.binkley.man.handler;

import hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution;
import org.axonframework.eventstore.supporting.VolatileEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@code TestConfiguration} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Configuration
public class TestConfiguration {
    private final List<AxonExecution> executions = new ArrayList<>();

    @Bean
    public Supplier<List<AxonExecution>> axonExecutions() {
        return () -> executions;
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
