package hm.binkley.man;

import hm.binkley.man.aggregate.Application;
import org.axonframework.auditing.AuditDataProvider;
import org.axonframework.auditing.AuditLogger;
import org.axonframework.auditing.AuditingInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Configuration
@ConditionalOnClass(CommandBus.class)
public class ApplicationConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus(
            final AuditingInterceptor auditingInterceptor) {
        final SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setDispatchInterceptors(
                singletonList(new BeanValidationInterceptor()));
        commandBus.setHandlerInterceptors(
                asList(new BeanValidationInterceptor(), auditingInterceptor));
        return commandBus;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditingInterceptor auditingInterceptor(
            final AuditDataProvider auditDataProvider,
            final AuditLogger auditLogger) {
        final AuditingInterceptor interceptor = new AuditingInterceptor();
        interceptor.setAuditDataProvider(auditDataProvider);
        interceptor.setAuditLogger(auditLogger);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditDataProvider auditDataProvider() {
        return new FlowIdAuditDataProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLogger auditLogger(final Consumer<AuditRecord> records) {
        return new RecordingAuditLogger(records);
    }

    @Bean
    @ConditionalOnMissingBean
    public Consumer<AuditRecord> auditRecordConsumer() {
        return record -> {};
    }

    @Bean
    @ConditionalOnMissingBean(CommandGateway.class)
    public CommandGatewayFactoryBean commandGatewayFactory(
            final CommandBus commandBus) {
        final CommandGatewayFactoryBean gatewayFactory
                = new CommandGatewayFactoryBean();
        gatewayFactory.setCommandBus(commandBus);
        return gatewayFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventStore eventStore()
            throws IOException {
        final SimpleEventFileResolver resolver = new SimpleEventFileResolver(
                File.createTempFile("axon", "events"));
        return new FileSystemEventStore(resolver);
    }

    @Bean
    public AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor(
            final CommandBus commandBus) {
        final AnnotationCommandHandlerBeanPostProcessor p
                = new AnnotationCommandHandlerBeanPostProcessor();
        p.setCommandBus(commandBus);
        return p;
    }

    @Bean
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(
            final EventBus eventBus) {
        final AnnotationEventListenerBeanPostProcessor p
                = new AnnotationEventListenerBeanPostProcessor();
        p.setEventBus(eventBus);
        return p;
    }

    @Bean(name = "applicationRepository")
    public EventSourcingRepository<Application> eventSourcingRepository(
            final EventBus eventBus, final EventStore eventStore,
            final CommandBus commandBus) {
        final EventSourcingRepository<Application> repository
                = new EventSourcingRepository<>(Application.class,
                eventStore);
        repository.setEventBus(eventBus);
        AggregateAnnotationCommandHandler
                .subscribe(Application.class, repository, commandBus);
        return repository;
    }
}
