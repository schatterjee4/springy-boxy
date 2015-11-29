package hm.binkley.man.audit;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.InterceptorChain;
import org.axonframework.unitofwork.UnitOfWork;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * {@code TrackingInterceptor} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TrackingInterceptor
        implements CommandHandlerInterceptor {
    @Nonnull
    private final Consumer<? super UnitOfWorkRecord> records;

    @Override
    public Object handle(final CommandMessage<?> commandMessage,
            final UnitOfWork unitOfWork,
            final InterceptorChain interceptorChain)
            throws Throwable {
        unitOfWork.registerListener(new TrackingUnitOfWorkListener(records));
        return interceptorChain.proceed();
    }
}
