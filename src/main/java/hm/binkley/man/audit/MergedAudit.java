package hm.binkley.man.audit;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.axonframework.commandhandling.CommandMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * {@code XXX} <b>needs documentation</b>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
@EqualsAndHashCode
@ToString
public final class MergedAudit {
    @Nonnull
    public final CommandMessage commandMessage;
    @Nonnull
    public final JoinPoint handler; // TODO: Aspect not getting this
    @Nullable
    public final Object returnValue;
    @Nullable
    public final Throwable failureCause;
    @Nonnull
    public final List<AxonExecution> events;

    public MergedAudit(@Nonnull final AuditRecord record,
            @Nonnull final List<AxonExecution> executions) {
        commandMessage = record.command;
        handler = executions.stream().
                filter(AxonExecution::isCommand).
                map(e -> e.handler).
                findFirst().
                orElse(null);
        returnValue = record.returnValue;
        failureCause = record.failureCause;
        events = executions.stream().
                filter(AxonExecution::isEvent).
                collect(toList());

        assert failureCause == lastOf(executions).failureCause
                : "Audit mismatch of command and event handlers";
    }

    public String correlationIdentifier() {
        return commandMessage.getIdentifier();
    }

    private static <T> T lastOf(final List<T> list) {
        return list.get(list.size() - 1);
    }
}