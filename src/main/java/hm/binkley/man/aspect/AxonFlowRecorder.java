package hm.binkley.man.aspect;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution.success;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleCommand;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleEvent;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleEventMessage;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;
import static org.axonframework.domain.GenericEventMessage.asEventMessage;

@Aspect
@Component
public class AxonFlowRecorder {
    private final Consumer<? super AxonExecution> executions;

    @Inject
    public AxonFlowRecorder(
            final Consumer<? super AxonExecution> executions) {
        this.executions = executions;
    }

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
        final EventMessage message = (EventMessage) pjp.getArgs()[0];
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
            final ProceedingJoinPoint pjp, final Message... messages)
            throws Throwable {
        final List<AxonExecution> executions = Stream.of(messages).
                map(thing -> success(action, thing, pjp)).
                collect(toList());
        try {
            executions.forEach(this.executions);
            return pjp.proceed();
        } catch (final Throwable t) {
            executions.forEach(execution -> execution.failure = t);
            throw t;
        }
    }

    public enum ExecutionAction {
        dispatchCommandMessage,
        handleCommandMessage,
        handleCommand,
        publishEventMessage,
        handleEventMessage,
        handleEvent
    }

    @AllArgsConstructor(access = PRIVATE)
    @ToString
    public static final class AxonExecution {
        static AxonExecution success(final ExecutionAction action,
                final Message thing, final JoinPoint handler) {
            return new AxonExecution(action, handler, thing, null);
        }

        static AxonExecution failure(final ExecutionAction action,
                final Message thing, final JoinPoint handler,
                final Throwable failure) {
            return new AxonExecution(action, handler, thing, failure);
        }

        @Nonnull
        public final ExecutionAction action;
        @Nonnull
        public final JoinPoint handler;
        @Nonnull
        public final Message message;
        @Nullable
        public Throwable failure; // TODO: Unhappy about mutable

        @SuppressWarnings("unchecked")
        public <U> Message<U> asMessage() {
            return (Message<U>) message;
        }

        public <U> U asDomain() {
            return this.<U>asMessage().getPayload();
        }

        @SuppressWarnings("unchecked")
        public <C> Optional<CommandMessage<C>> asCommandMessage() {
            switch (action) {
            case dispatchCommandMessage:
            case handleCommandMessage:
            case handleCommand:
                return Optional.of((CommandMessage<C>) message);
            default: // Oh for proper case statements
                return Optional.empty();
            }
        }

        public <C> Optional<C> asCommand() {
            return this.<C>asCommandMessage().
                    map(Message::getPayload);
        }

        @SuppressWarnings("unchecked")
        public <E> Optional<EventMessage<E>> asEventMessage() {
            switch (action) {
            case publishEventMessage:
            case handleEventMessage:
            case handleEvent:
                return Optional.of((EventMessage<E>) message);
            default: // Oh for proper case statements
                return Optional.empty();
            }
        }

        public <C> Optional<C> asEvent() {
            return this.<C>asEventMessage().
                    map(Message::getPayload);
        }
    }
}
