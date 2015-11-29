package hm.binkley;

import hm.binkley.man.audit.AxonExecution;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Do something useful
 */
@SpringBootApplication
public class Main {
    private static final Logger logger = getLogger(Main.class);

    public static void main(final String... args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Consumer<? super AxonExecution> axonExecutionConsumer() {
        return execution -> logger.debug(execution.toString());
    }
}
