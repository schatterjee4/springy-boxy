package hm.binkley.man.audit;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.domain.AggregateRoot;
import org.axonframework.domain.EventMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkListenerAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static hm.binkley.man.audit.UnitOfWorkRecord.success;

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
