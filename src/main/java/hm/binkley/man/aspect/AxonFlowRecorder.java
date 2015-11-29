package hm.binkley.man.aspect;

import hm.binkley.man.audit.HandlerExecutionRecord;
import hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction;
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

import static hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction.dispatchCommandMessage;
import static hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction.handleCommand;
import static hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction.handleCommandMessage;
import static hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction.handleEvent;
import static hm.binkley.man.audit.HandlerExecutionRecord.ExecutionAction.handleEventMessage;
import static hm.binkley.man.audit.HandlerExecutionRecord.failure;
import static hm.binkley.man.audit.HandlerExecutionRecord.success;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;
import static org.axonframework.domain.GenericEventMessage.asEventMessage;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AxonFlowRecorder {
    private final Consumer<? super HandlerExecutionRecord> consumer;

    @Pointcut(
            "execution(void org.axonframework.commandhandling.CommandBus.dispatch(..))")
    public void handleDispatchCommandMessage() {}

    @Pointcut(
            "execution(* org.axonframework.commandhandling.CommandHandler.handle(..))")
    public void handleCommandMessage() {}

    /** @todo Does not match on ctor with Spring AOP */
    @Pointcut(
            "@annotation(org.axonframework.commandhandling.annotation.CommandHandler)")
    public void handleCommand() {}

    @Pointcut(
            "execution(* org.axonframework.eventhandling.EventListener.handle(..))")
    public void handleEventMessage() {}

    @Pointcut(
            "@annotation(org.axonframework.eventhandling.annotation.EventHandler)")
    public void handleEvent() {}

    /** @todo Re-enable when dispatched command has an ID */
    // @Around("handleDispatchCommandMessage()")
    public Object logDispatchCommandMessage(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Message message = (Message) pjp.getArgs()[0];
        return proceedWithRecording(dispatchCommandMessage, pjp, message);
    }

    @Around("handleCommandMessage()")
    public Object logHandleCommandMessage(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Message message = (Message) pjp.getArgs()[0];
        return proceedWithRecording(handleCommandMessage, pjp, message);
    }

    /** @todo Never actually called, so handler sigature is lost */
    @Around("handleCommand()")
    public Object logHandleCommand(final ProceedingJoinPoint pjp)
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

    /** @todo Events happen before commands in trail */
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
