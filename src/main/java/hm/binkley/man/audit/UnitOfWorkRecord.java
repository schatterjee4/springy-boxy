package hm.binkley.man.audit;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.common.annotation.MessageHandlerInvocationException;
import org.axonframework.domain.AggregateRoot;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static org.axonframework.auditing.CorrelationAuditDataProvider.DEFAULT_CORRELATION_KEY;

/**
 * {@code UnitOfWorkRecord} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
@ToString
public final class UnitOfWorkRecord {
    public final Set<AggregateRoot> aggregateRoots;
    public final List<EventMessage> eventMessages;
    public final Throwable failureCause;

    static UnitOfWorkRecord success() {
        return new UnitOfWorkRecord(new HashSet<>(), new ArrayList<>(), null);
    }

    UnitOfWorkRecord failure(final Throwable failureCause) {
        return new UnitOfWorkRecord(aggregateRoots, eventMessages,
                unwrap(failureCause));
    }

    public Stream<String> getCommandIdentifiers() {
        return eventMessages.stream().
                map(UnitOfWorkRecord::getCommandIdentifier);
    }

    private static String getCommandIdentifier(final Message message) {
        return (String) message.getMetaData().get(DEFAULT_CORRELATION_KEY);
    }

    private static Throwable unwrap(Throwable failureCause) {
        if (failureCause instanceof MessageHandlerInvocationException)
            failureCause = failureCause.getCause();
        if (failureCause instanceof InvocationTargetException)
            failureCause = failureCause.getCause();
        return failureCause;
    }
}