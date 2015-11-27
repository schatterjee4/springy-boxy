package hm.binkley.man;

import hm.binkley.man.aggregate.Application;
import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.TrackingUnitOfWorkListener;
import hm.binkley.man.audit.TrackingUnitOfWorkListener.UnitOfWorkRecord;
import org.axonframework.auditing.AuditDataProvider;
import org.axonframework.auditing.AuditLogger;
import org.axonframework.auditing.AuditingInterceptor;
import org.axonframework.auditing.CorrelationAuditDataProvider;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
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
import static org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler.subscribe;

// URP
@Configuration
@ConditionalOnClass(CommandBus.class)
public class ApplicationConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus(
            final AuditingInterceptor auditingInterceptor,
            final Consumer<? super UnitOfWorkRecord> records) {
        final SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setDispatchInterceptors(
                singletonList(new BeanValidationInterceptor()));
        commandBus.setHandlerInterceptors(
                asList(new BeanValidationInterceptor(), auditingInterceptor,
                        (commandMessage, unitOfWork, interceptorChain) -> {
                            unitOfWork.registerListener(
                                    new TrackingUnitOfWorkListener(records));
                            return interceptorChain.proceed();
                        }));
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
        return new CorrelationAuditDataProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public Consumer<? super AuditRecord> auditRecordConsumer() {
        return record -> {};
    }

    @Bean
    @ConditionalOnMissingBean
    public Consumer<? super UnitOfWorkRecord> unitOfWorkRecordConsumer() {
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
        final AnnotationCommandHandlerBeanPostProcessor processor
                = new AnnotationCommandHandlerBeanPostProcessor();
        processor.setCommandBus(commandBus);
        return processor;
    }

    @Bean
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(
            final EventBus eventBus) {
        final AnnotationEventListenerBeanPostProcessor processor
                = new AnnotationEventListenerBeanPostProcessor();
        processor.setEventBus(eventBus);
        return processor;
    }

    @Bean
    public EventSourcingRepository<Application> applicationRepository(
            final EventBus eventBus, final EventStore eventStore,
            final CommandBus commandBus) {
        final EventSourcingRepository<Application> repository
                = new EventSourcingRepository<>(Application.class,
                eventStore);
        repository.setEventBus(eventBus);
        subscribe(Application.class, repository, commandBus);
        return repository;
    }
}
