package hm.binkley.man.aspect;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.function.Consumer;

import static hm.binkley.man.aspect.AxonFlowRecorder.Action.dispatchCommand;
import static hm.binkley.man.aspect.AxonFlowRecorder.Action.handleCommand;
import static hm.binkley.man.aspect.AxonFlowRecorder.Action.handledEvent;
import static hm.binkley.man.aspect.AxonFlowRecorder.Action.publishEvent;
import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class AxonFlowRecorder {
    private static final Logger logger = getLogger(AxonFlowRecorder.class);
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
        final Signature handler = pjp.getSignature();
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        logger.debug("DISPATCH {}({}) @ {}", message, message.getPayload(),
                handler);

        return proceedWithRecording(dispatchCommand, pjp, message);
    }

    @Around("handleCommandMessage()")
    public Object logCommandHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        return proceedWithRecording(handleCommand, pjp, message);
    }

    @Around("handleCommand()")
    public Object logCommand(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final Object command = pjp.getArgs()[0];
        try {
            final Object proceed = pjp.proceed();
            logger.debug("SUCCESS");
            return proceed;
        } catch (final Throwable t) {
            logger.warn("FAILED " + t, t);
            throw t;
        }
    }

    @Around("publishEventMessage()")
    public Object logEventPublish(final ProceedingJoinPoint pjp)
            throws Throwable {
        final EventMessage[] messages = (EventMessage[]) pjp.getArgs()[0];
        return proceedWithRecording(publishEvent, pjp, messages);
    }

    @Around("handleEventMessage()")
    public Object logEventHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final EventMessage message = (EventMessage) pjp.getArgs()[0];
        return proceedWithRecording(handledEvent, pjp, message);
    }

    @Around("handleEvent()")
    public Object logEvent(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final Object event = pjp.getArgs()[0];
        logger.debug("HANDLE {} @ {}", event, handler);

        try {
            final Object proceed = pjp.proceed();
            logger.debug("SUCCESS");
            return proceed;
        } catch (final Throwable t) {
            logger.warn("FAILED " + t, t);
            throw t;
        }
    }

    private Object proceedWithRecording(final Action action,
            final ProceedingJoinPoint pjp, final Message... messages)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            for (final Message message : messages)
                executions
                        .accept(AxonExecution.of(action, message, pjp, null));
            return proceed;
        } catch (final Throwable t) {
            for (final Message message : messages)
                executions.accept(AxonExecution.of(action, message, pjp, t));
            throw t;
        }
    }

    public enum Action {
        dispatchCommand,
        handleCommand,
        publishEvent,
        handledEvent,
    }

    @RequiredArgsConstructor(staticName = "of")
    @ToString
    public static final class AxonExecution {
        @Nonnull
        public final Action action;
        @Nonnull
        public final Message message;
        @Nonnull
        public final JoinPoint handler;
        @Nullable
        public final Throwable failure;
    }
}
