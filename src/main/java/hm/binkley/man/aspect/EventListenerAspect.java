package hm.binkley.man.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.function.Consumer;

import static java.lang.System.out;

@Aspect
public class EventListenerAspect {
    private final Consumer<ProceedingJoinPoint> log = out::println;

    @Pointcut(
            "@annotation(org.axonframework.eventhandling.annotation.EventHandler)")
    public void eventListener() {}

    @Around("eventListener()")
    public Object logEventListener(final ProceedingJoinPoint pjp)
            throws Throwable {
        log.accept(pjp);
        return pjp.proceed();
    }
}
