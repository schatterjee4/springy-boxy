package hm.binkley.man.aspect;

import hm.binkley.man.aspect.EventListenerRecorder.Execution;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class EventListenerRecorder
        extends AbstractList<Execution> {
    private final List<Execution> executions = new ArrayList<>();

    @Pointcut(
            "@annotation(org.axonframework.eventhandling.annotation.EventHandler)")
    public void eventListener() {}

    @Around("eventListener()")
    public Object logEventListener(final ProceedingJoinPoint pjp)
            throws Throwable {
        try {
            final Object proceed = pjp.proceed();
            executions.add(Execution.of(pjp, null));
            return proceed;
        } catch (final Throwable t) {
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

    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    @ToString
    public static final class Execution {
        @Nonnull
        public final JoinPoint execution;
        @Nullable
        public final Throwable failure;
    }
}
