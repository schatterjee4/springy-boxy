package hm.binkley.man.aspect;

import lombok.RequiredArgsConstructor;
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
import java.util.function.Consumer;

import static hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution.failure;
import static hm.binkley.man.aspect.AxonFlowRecorder.AxonExecution.success;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.dispatchCommandMessage;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleCommand;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleCommandMessage;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleEvent;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.handleEventMessage;
import static hm.binkley.man.aspect.AxonFlowRecorder.ExecutionAction.publishEventMessage;
import static lombok.AccessLevel.PRIVATE;

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
            "execution(public * org.axonframework.commandhandling.CommandBus.dispatch(..))")
    public void dispatchCommandMessage() {}

    @Pointcut(
            "execution(public * org.axonframework.commandhandling.CommandHandler.handle(..))")
    public void handleCommandMessage() {}

    @Pointcut(
            "@annotation(org.axonframework.commandhandling.annotation.CommandHandler)")
    public void handleCommand() {}

    @Pointcut(
            "execution(public * org.axonframework.eventhandling.EventBus.publish(..))")
    public void publishEventMessage() {}

    @Pointcut(
            "execution(public * org.axonframework.eventhandling.EventListener.handle(..))")
    public void handleEventMessage() {}

    @Pointcut(
            "@annotation(org.axonframework.eventhandling.annotation.EventHandler)")
    public void handleEvent() {}

    @Around("dispatchCommandMessage()")
    public Object logCommandDispatch(final ProceedingJoinPoint pjp)
            throws Throwable {
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        return proceedWithRecording(dispatchCommandMessage, pjp, message);
    }

    @Around("handleCommandMessage()")
    public Object logCommandHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        return proceedWithRecording(handleCommandMessage, pjp, message);
    }

    @Around("handleCommand()")
    public Object logCommand(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Object command = pjp.getArgs()[0];
        return proceedWithRecording(handleCommand, pjp, command);
    }

    @Around("publishEventMessage()")
    public Object logEventPublish(final ProceedingJoinPoint pjp)
            throws Throwable {
        final EventMessage[] messages = (EventMessage[]) pjp.getArgs()[0];
        return proceedWithRecording(publishEventMessage, pjp, messages);
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
        return proceedWithRecording(handleEvent, pjp, event);
    }

    @SafeVarargs
    private final <T> Object proceedWithRecording(
            final ExecutionAction executionAction,
            final ProceedingJoinPoint pjp, final T... things)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            for (final T thing : things)
                executions.accept(success(executionAction, thing, pjp));
            return proceed;
        } catch (final Throwable t) {
            for (final T thing : things)
                executions.accept(failure(executionAction, thing, pjp, t));
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

    @RequiredArgsConstructor(access = PRIVATE)
    @SuppressWarnings("unchecked")
    @ToString
    public static final class AxonExecution<T> {
        static <T> AxonExecution<T> success(
                final ExecutionAction executionAction, final T thing,
                final JoinPoint handler) {
            return new AxonExecution<>(executionAction, thing, handler, null);
        }

        static <T> AxonExecution<T> failure(
                final ExecutionAction executionAction, final T thing,
                final JoinPoint handler, final Throwable failure) {
            return new AxonExecution<>(executionAction, thing, handler,
                    failure);
        }

        @Nonnull
        public final ExecutionAction executionAction;
        @Nonnull
        public final T thing;
        @Nonnull
        public final JoinPoint handler;
        @Nullable
        public final Throwable failure;

        public <C> CommandMessage<C> asCommandMessage() {
            switch (executionAction) {
            case dispatchCommandMessage:
            case handleCommandMessage:
                return (CommandMessage<C>) thing;
            default: // Oh for proper case statements
                throw new IllegalStateException();
            }
        }

        public <C> C asCommand() {
            switch (executionAction) {
            case dispatchCommandMessage:
            case handleCommandMessage:
                return asDomain();
            case handleCommand:
                return (C) thing;
            default: // Oh for proper case statements
                throw new IllegalStateException();
            }
        }

        public <E> EventMessage<E> asEventMessage() {
            switch (executionAction) {
            case publishEventMessage:
            case handleEventMessage:
                return (EventMessage<E>) thing;
            default: // Oh for proper case statements
                throw new IllegalStateException();
            }
        }

        public <C> C asEvent() {
            switch (executionAction) {
            case publishEventMessage:
            case handleEventMessage:
                return asDomain();
            case handleEvent:
                return (C) thing;
            default: // Oh for proper case statements
                throw new IllegalStateException();
            }
        }

        public <U> U asDomain() {
            switch (executionAction) {
            case dispatchCommandMessage:
            case handleCommandMessage:
            case publishEventMessage:
            case handleEventMessage: // Oh for proper case statements
                return ((Message<U>) thing).getPayload();
            case handleCommand:
            case handleEvent:
                return (U) thing;
            default:
                throw new Error("BUG: Missing case");
            }
        }
    }
}
