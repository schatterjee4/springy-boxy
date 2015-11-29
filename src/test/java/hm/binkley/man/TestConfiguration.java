package hm.binkley.man;

import hm.binkley.man.aggregate.CheckedTestFailureAggregate;
import hm.binkley.man.aggregate.TestSuccessAggregate;
import hm.binkley.man.aggregate.UncheckedTestFailureAggregate;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.AxonExecution;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.supporting.VolatileEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.function.Consumer;

import static org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler.subscribe;

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
    private final ArrayList<AuditRecord> records = new ArrayList<>();

    @Bean
    public ArrayList<AxonExecution> axonExecutions() {
        return executions;
    }

    @Bean
    public ArrayList<AuditRecord> auditRecords() { return records; }

    @Bean
    @Primary
    public Consumer<? super AxonExecution> axonExecutionConsumer() {
        return executions::add;
    }

    @Bean
    public Consumer<AuditRecord> auditRecordConsumer() {
        return records::add;
    }

    @Bean
    public VolatileEventStore eventStore() {
        return new VolatileEventStore();
    }

    @Bean
    public EventSourcingRepository<TestSuccessAggregate> testSuccessRepository(
            final EventBus eventBus, final EventStore eventStore,
            final CommandBus commandBus) {
        final EventSourcingRepository<TestSuccessAggregate> repository
                = new EventSourcingRepository<>(TestSuccessAggregate.class,
                eventStore);
        repository.setEventBus(eventBus);
        subscribe(TestSuccessAggregate.class, repository, commandBus);
        return repository;
    }

    @Bean
    public EventSourcingRepository<CheckedTestFailureAggregate> checkedTestFailureAggregateRepository(
            final EventBus eventBus, final EventStore eventStore,
            final CommandBus commandBus) {
        final EventSourcingRepository<CheckedTestFailureAggregate> repository
                = new EventSourcingRepository<>(
                CheckedTestFailureAggregate.class, eventStore);
        repository.setEventBus(eventBus);
        subscribe(CheckedTestFailureAggregate.class, repository, commandBus);
        return repository;
    }

    @Bean
    public EventSourcingRepository<UncheckedTestFailureAggregate> uncheckedTestFailureAggregateRepository(
            final EventBus eventBus, final EventStore eventStore,
            final CommandBus commandBus) {
        final EventSourcingRepository<UncheckedTestFailureAggregate>
                repository = new EventSourcingRepository<>(
                UncheckedTestFailureAggregate.class, eventStore);
        repository.setEventBus(eventBus);
        subscribe(UncheckedTestFailureAggregate.class, repository,
                commandBus);
        return repository;
    }
}
