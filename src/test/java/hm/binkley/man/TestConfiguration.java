package hm.binkley.man;

import hm.binkley.man.aggregate.CheckedTestFailureAggregate;
import hm.binkley.man.aggregate.TestSuccessAggregate;
import hm.binkley.man.aggregate.UncheckedTestFailureAggregate;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.ExecutionRecord;
import hm.binkley.man.audit.UnitOfWorkRecord;
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
    private final ArrayList<ExecutionRecord> handlerExecutionRecords
            = new ArrayList<>();
    private final ArrayList<AuditRecord> auditRecords = new ArrayList<>();
    private final ArrayList<UnitOfWorkRecord> unitOfWorkRecords
            = new ArrayList<>();

    @Bean
    public ArrayList<ExecutionRecord> executionRecords() {
        return handlerExecutionRecords;
    }

    @Bean
    public ArrayList<AuditRecord> auditRecords() { return auditRecords; }

    @Bean
    public ArrayList<UnitOfWorkRecord> unitOfWorkRecords() {
        return unitOfWorkRecords;
    }

    @Bean
    @Primary
    public Consumer<? super ExecutionRecord> axonExecutionConsumer() {
        return handlerExecutionRecords::add;
    }

    @Bean
    public Consumer<? super AuditRecord> auditRecordConsumer() {
        return auditRecords::add;
    }

    @Bean
    public Consumer<? super UnitOfWorkRecord> unitOfWorkRecordConsumer() {
        return unitOfWorkRecords::add;
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
