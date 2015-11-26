package hm.binkley.man;

import hm.binkley.man.aggregate.Application;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.domain.MetaData;
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
import java.util.Arrays;
import java.util.HashMap;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;

@Configuration
@ConditionalOnClass(CommandBus.class)
public class ApplicationConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus() {
        final SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setDispatchInterceptors(
                Arrays.<CommandDispatchInterceptor>asList(
                        new BeanValidationInterceptor(),
                        new FlowTrackingCommandDispatchInterceptor()));
        commandBus.setHandlerInterceptors(
                singletonList(new BeanValidationInterceptor()));
        return commandBus;
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

    private static class FlowTrackingCommandDispatchInterceptor
            implements CommandDispatchInterceptor {
        @Override
        public CommandMessage<?> handle(final CommandMessage<?> message) {
            return message
                    .andMetaData(new DispatchMetaData(message.getMetaData()));
        }
    }

    private static class DispatchMetaData
            extends HashMap<String, Object> {
        DispatchMetaData(final MetaData metaData) {
            if (!metaData.containsKey("flow-id"))
                put("flow-id", randomUUID());
        }
    }
}
