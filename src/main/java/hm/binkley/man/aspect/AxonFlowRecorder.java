package hm.binkley.man.aspect;

import hm.binkley.man.audit.AxonExecution;
import hm.binkley.man.audit.AxonExecution.ExecutionAction;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Consumer;

import static hm.binkley.man.audit.AxonExecution.ExecutionAction.handleCommand;
import static hm.binkley.man.audit.AxonExecution.ExecutionAction.handleEvent;
import static hm.binkley.man.audit.AxonExecution.ExecutionAction.handleEventMessage;
import static hm.binkley.man.audit.AxonExecution.failure;
import static hm.binkley.man.audit.AxonExecution.success;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;
import static org.axonframework.domain.GenericEventMessage.asEventMessage;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AxonFlowRecorder {
    private final Consumer<? super AxonExecution> consumer;

    @Pointcut(
            "@annotation(org.axonframework.commandhandling.annotation.CommandHandler)")
    public void handleCommand() {}

    @Pointcut(
            "execution(* org.axonframework.eventhandling.EventListener.handle(..))")
    public void handleEventMessage() {}

    @Pointcut(
            "@annotation(org.axonframework.eventhandling.annotation.EventHandler)")
    public void handleEvent() {}

    @Around("handleCommand()")
    public Object logCommand(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Object command = pjp.getArgs()[0];
        final Optional<CommandMessage> message = findMessage(
                CommandMessage.class, pjp.getArgs());
        return proceedWithRecording(handleCommand, pjp,
                message.orElseGet(() -> asCommandMessage(command)));
    }

    @Around("handleEventMessage()")
    public Object logEventHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Message message = (Message) pjp.getArgs()[0];
        return proceedWithRecording(handleEventMessage, pjp, message);
    }

    @Around("handleEvent()")
    public Object logEvent(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Object event = pjp.getArgs()[0];
        final Optional<EventMessage> message = findMessage(EventMessage.class,
                pjp.getArgs());
        return proceedWithRecording(handleEvent, pjp,
                message.orElseGet(() -> asEventMessage(event)));
    }

    private static <M extends Message> Optional<M> findMessage(
            final Class<M> type, final Object[] args) {
        for (int i = 1; i < args.length; ++i)
            if (type.isAssignableFrom(args[i].getClass()))
                return Optional.of(type.cast(args[i]));
        return Optional.empty();
    }

    private Object proceedWithRecording(final ExecutionAction action,
            final ProceedingJoinPoint pjp, final Message message)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            consumer.accept(success(action, message, pjp));
            return proceed;
        } catch (final Throwable t) {
            consumer.accept(failure(action, message, pjp, t));
            throw t;
        }
    }
}
