package hm.binkley.man.handler;

import org.axonframework.eventstore.supporting.VolatileEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code TestConfiguration} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Configuration
public class TestConfiguration {
    @Bean
    public VolatileEventStore eventStore() {
        return new VolatileEventStore();
    }
}
