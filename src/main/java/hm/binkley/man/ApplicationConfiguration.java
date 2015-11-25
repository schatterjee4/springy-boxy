package hm.binkley.man;

import hm.binkley.man.aggregate.Application;
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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

import static java.util.Collections.singletonList;

@Configuration
@ConditionalOnClass(CommandBus.class)
public class ApplicationConfiguration {
    @PostConstruct
    public void register()
            throws IOException {
        AggregateAnnotationCommandHandler.subscribe(Application.class,
                eventSourcingRepository(eventBus(), eventStore()),
                commandBus());
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus() {
        final SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setHandlerInterceptors(
                singletonList(new BeanValidationInterceptor()));
        return commandBus;
    }

    @Bean
    @ConditionalOnMissingBean(CommandGateway.class)
    public CommandGatewayFactoryBean commandGateway(
            final CommandBus commandBus) {
        final CommandGatewayFactoryBean factory
                = new CommandGatewayFactoryBean();
        factory.setCommandBus(commandBus);
        return factory;
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

    @Bean(name = "applicationRepository")
    @ConditionalOnMissingBean
    public EventSourcingRepository<Application> eventSourcingRepository(
            final EventBus eventBus, final EventStore eventStore) {
        final EventSourcingRepository<Application> eventSourcingRepository
                = new EventSourcingRepository<>(Application.class,
                eventStore);
        eventSourcingRepository.setEventBus(eventBus);
        return eventSourcingRepository;
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
}
