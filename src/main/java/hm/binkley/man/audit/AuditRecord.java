package hm.binkley.man.audit;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * {@code AuditRecord} <b>needs documentation</b>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
@ToString
public final class AuditRecord {
    public final CommandMessage command;
    public final Object returnValue;
    public final Throwable failureCause;
    public final List<EventMessage> events;

    public static AuditRecord recordSuccess(final CommandMessage command,
            final Object returnValue, final List<EventMessage> events) {
        return new AuditRecord(command, returnValue, null, events);
    }

    public static AuditRecord recordFailure(final CommandMessage command,
            final Throwable failureCause, final List<EventMessage> events) {
        return new AuditRecord(command, null, failureCause, events);
    }
}
