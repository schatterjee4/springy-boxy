package hm.binkley.man.aspect;

import hm.binkley.man.aspect.AxonFlowRecorder.Execution;
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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class AxonFlowRecorder
        extends AbstractList<Execution> {
    private static final Logger logger = getLogger(AxonFlowRecorder.class);
    private final List<Execution> executions = new ArrayList<>();

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

        return proceedWithRecording(pjp, "DISPATCH COMMAND", message);
    }

    @Around("handleCommandMessage()")
    public Object logCommandHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        logger.debug("HANDLE {}({}) @ {}", message, message.getPayload(),
                handler);

        return proceedWithRecording(pjp, "HANDLE COMMAND", message);
    }

    @Around("handleCommand()")
    public Object logCommand(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final Object command = pjp.getArgs()[0];
        logger.debug("HANDLE {} @ {}", command, handler);

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
        final Signature handler = pjp.getSignature();
        final EventMessage[] messages = (EventMessage[]) pjp.getArgs()[0];
        for (final EventMessage message : messages)
            logger.debug("PUBLISH {}({}) @ {}", message, message.getPayload(),
                    handler);

        return proceedWithRecording(pjp, "PUBLISH EVENT", messages);
    }

    @Around("handleEventMessage()")
    public Object logEventHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final EventMessage message = (EventMessage) pjp.getArgs()[0];
        logger.debug("HANDLE {}({}) @ {}", message, message.getPayload(),
                handler);

        return proceedWithRecording(pjp, "HANDLE EVENT", message);
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

    private Object proceedWithRecording(final ProceedingJoinPoint pjp,
            final String tag, final Message... messages)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            for (final Message message : messages)
                executions.add(Execution.of(message, pjp, null));
            logger.debug("SUCCESS " + tag);
            return proceed;
        } catch (final Throwable t) {
            logger.warn("FAILED " + tag + " " + t, t);
            for (final Message message : messages)
                executions.add(Execution.of(message, pjp, t));
            throw t;
        }
    }

    @Override
    public Execution get(final int index) {
        return executions.get(index);
    }

    @Override
    public int size() {
        return executions.size();
    }

    @RequiredArgsConstructor(staticName = "of")
    @ToString
    public static final class Execution {
        @Nonnull
        public final Message message;
        @Nonnull
        public final JoinPoint handler;
        @Nullable
        public final Throwable failure;
    }
}
