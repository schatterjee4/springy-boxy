package hm.binkley.man.audit;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.common.annotation.MessageHandlerInvocationException;
import org.axonframework.domain.EventMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
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
    @Nonnull
    public final CommandMessage command;
    @Nullable
    public final Object returnValue;
    @Nullable
    public final Throwable failureCause;
    @Nonnull
    public final List<EventMessage> events;

    public static AuditRecord recordSuccess(final CommandMessage command,
            final Object returnValue, final List<EventMessage> events) {
        return new AuditRecord(command, returnValue, null, events);
    }

    public static AuditRecord recordFailure(final CommandMessage command,
            final Throwable failureCause, final List<EventMessage> events) {
        return new AuditRecord(command, null, unwrap(failureCause), events);
    }

    public String getCommandIdentifier() {
        return command.getIdentifier();
    }

    private static Throwable unwrap(Throwable failureCause) {
        if (failureCause instanceof MessageHandlerInvocationException)
            failureCause = failureCause.getCause();
        if (failureCause instanceof InvocationTargetException)
            failureCause = failureCause.getCause();
        return failureCause;
    }
}
