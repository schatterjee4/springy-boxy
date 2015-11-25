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
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;

@Aspect
@Component
public class AxonFlowRecorder
        extends AbstractList<Execution> {
    private final List<Execution> executions = new ArrayList<>();

    @Pointcut(
            "execution(public * org.axonframework.commandhandling.CommandBus.dispatch(..))")
    public void dispatchCommandMessage() {}

    @Pointcut(
            "execution(public * org.axonframework.commandhandling.CommandHandler.handle(..))")
    public void handleCommandMessage() {}

    @Pointcut(
            "execution(public * org.axonframework.eventhandling.EventBus.publish(..))")
    public void publishEventMessage() {}

    @Pointcut(
            "execution(public * org.axonframework.eventhandling.EventListener.handle(..))")
    public void handleEventMessage() {}

    @Around("dispatchCommandMessage()")
    public Object logCommandDispatch(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        out.println(
                format("DISPATCH %s(%s) @ %s", message, message.getPayload(),
                        handler));

        return proceedWithLogging(pjp, "DISPATCH COMMAND");
    }

    @Around("handleCommandMessage()")
    public Object logCommandHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final CommandMessage message = (CommandMessage) pjp.getArgs()[0];
        out.println(
                format("HANDLE %s(%s) @ %s", message, message.getPayload(),
                        handler));

        return proceedWithLogging(pjp, "HANDLE COMMAND");
    }

    @Around("publishEventMessage()")
    public Object logEventPublish(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final List<EventMessage> messages = asList(
                (EventMessage[]) pjp.getArgs()[0]);
        messages.stream().
                map(message -> format("PUBLISH %s(%s) @ %s", message,
                        message.getPayload(), handler)).
                forEach(out::println);

        return proceedWithLogging(pjp, "PUBLISH EVENT");
    }

    @Around("handleEventMessage()")
    public Object logEventHandle(final ProceedingJoinPoint pjp)
            throws Throwable {
        final Signature handler = pjp.getSignature();
        final EventMessage message = (EventMessage) pjp.getArgs()[0];
        out.println(
                format("HANDLE %s(%s) @ %s", message, message.getPayload(),
                        handler));

        return proceedWithLogging(pjp, "HANDLE EVENT");
    }

    private Object proceedWithLogging(final ProceedingJoinPoint pjp,
            final String tag)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            executions.add(Execution.of(pjp, null));
            out.println("SUCCESS " + tag);
            return proceed;
        } catch (final Throwable t) {
            out.println("FAILED " + tag + " " + t);
            executions.add(Execution.of(pjp, t));
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
        public final JoinPoint handler;
        @Nullable
        public final Throwable failure;
    }
}
