package hm.binkley.man.audit;

import lombok.RequiredArgsConstructor;
import org.axonframework.auditing.AuditLogger;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static hm.binkley.man.audit.AuditRecord.recordFailure;
import static hm.binkley.man.audit.AuditRecord.recordSuccess;

/**
 * {@code RecordingAuditLogger} <b>needs documentation</b>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RecordingAuditLogger
        implements AuditLogger {
    @Nonnull
    private final Consumer<? super AuditRecord> records;

    @Override
    public void logSuccessful(final CommandMessage<?> command,
            final Object returnValue, final List<EventMessage> events) {
        records.accept(recordSuccess(command, returnValue, events));
    }

    @Override
    public void logFailed(final CommandMessage<?> command,
            final Throwable failureCause, final List<EventMessage> events) {
        records.accept(recordFailure(command, failureCause, events));
    }
}
