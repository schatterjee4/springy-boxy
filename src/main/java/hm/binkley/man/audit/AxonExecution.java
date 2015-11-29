package hm.binkley.man.audit;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static hm.binkley.man.audit.AxonExecution.ExecutionAction.handleCommand;
import static lombok.AccessLevel.PRIVATE;
import static org.axonframework.auditing.CorrelationAuditDataProvider.DEFAULT_CORRELATION_KEY;

/**
 * {@code AxonExecution} <b>needs documentation</b>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
@RequiredArgsConstructor(access = PRIVATE)
@ToString
public final class AxonExecution {
    @Nonnull
    public final ExecutionAction action;
    @Nonnull
    public final JoinPoint handler;
    @Nonnull
    public final Message message;
    @Nullable
    public final Throwable failureCause;

    public String getCommandIdentifier() {
        return (String) message.getMetaData().get(DEFAULT_CORRELATION_KEY);
    }

    public static AxonExecution success(final ExecutionAction action,
            final Message thing, final JoinPoint handler) {
        return new AxonExecution(action, handler, thing, null);
    }

    public static AxonExecution failure(final ExecutionAction action,
            final Message thing, final JoinPoint handler,
            final Throwable failure) {
        return new AxonExecution(action, handler, thing, failure);
    }

    public boolean isCommand() {
        return handleCommand == action;
    }

    public boolean isEvent() {
        switch (action) {
        case handleEventMessage:
        case handleEvent:
            return true;
        default:
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <U> Message<U> asMessage() {
        return (Message<U>) message;
    }

    public <U> U asDomain() {
        return this.<U>asMessage().getPayload();
    }

    @SuppressWarnings("unchecked")
    public <C> Optional<CommandMessage<C>> asCommandMessage() {
        switch (action) {
        case handleCommand:
            return Optional.of((CommandMessage<C>) message);
        default: // Oh for proper case statements
            return Optional.empty();
        }
    }

    public <C> Optional<C> asCommand() {
        return this.<C>asCommandMessage().
                map(Message::getPayload);
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<EventMessage<E>> asEventMessage() {
        switch (action) {
        case handleEventMessage:
        case handleEvent:
            return Optional.of((EventMessage<E>) message);
        default: // Oh for proper case statements
            return Optional.empty();
        }
    }

    public <C> Optional<C> asEvent() {
        return this.<C>asEventMessage().
                map(Message::getPayload);
    }

    public enum ExecutionAction {
        handleCommand,
        handleEventMessage,
        handleEvent
    }
}
