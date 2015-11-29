package hm.binkley.man.audit;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.common.annotation.MessageHandlerInvocationException;
import org.axonframework.domain.AggregateRoot;
import org.axonframework.domain.EventMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkListenerAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static hm.binkley.man.audit.TrackingUnitOfWorkListener.UnitOfWorkRecord.success;
import static lombok.AccessLevel.PRIVATE;

/**
 * {@code TrackingUnitOfWorkListener} <b>needs documentation</b>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString
public final class TrackingUnitOfWorkListener
        extends UnitOfWorkListenerAdapter {
    private static final ThreadLocal<UnitOfWorkRecord> currentRecord
            = ThreadLocal.withInitial(UnitOfWorkRecord::success);

    @Nonnull
    private final Consumer<? super UnitOfWorkRecord> records;

    @EqualsAndHashCode
    @RequiredArgsConstructor(access = PRIVATE)
    @ToString
    public static final class UnitOfWorkRecord {
        public final Set<AggregateRoot> aggregateRoots;
        public final List<EventMessage> eventMessages;
        public final Throwable failureCause;

        static UnitOfWorkRecord success() {
            return new UnitOfWorkRecord(new HashSet<>(), new ArrayList<>(),
                    null);
        }

        UnitOfWorkRecord failure(final Throwable failureCause) {
            if (failureCause instanceof MessageHandlerInvocationException)
                return new UnitOfWorkRecord(aggregateRoots, eventMessages,
                        failureCause.getCause().getCause());
            else
                return new UnitOfWorkRecord(aggregateRoots, eventMessages,
                        failureCause);
        }
    }

    @Override
    public void afterCommit(final UnitOfWork unitOfWork) {
        records.accept(currentRecord.get());
        currentRecord.set(success());
    }

    @Override
    public void onRollback(final UnitOfWork unitOfWork,
            final Throwable failureCause) {
        records.accept(currentRecord.get().failure(failureCause));
        currentRecord.set(success());
    }

    @Override
    public void onPrepareCommit(final UnitOfWork unitOfWork,
            final Set<AggregateRoot> aggregateRoots,
            final List<EventMessage> events) {
        final UnitOfWorkRecord record = currentRecord.get();
        record.aggregateRoots.addAll(aggregateRoots);
        record.eventMessages.addAll(events);
    }
}
