package hm.binkley.man.audit;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.common.annotation.MessageHandlerInvocationException;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

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
public final class HandlerExecutionRecord {
    @Nonnull
    public final ExecutionAction action;
    @Nonnull
    public final JoinPoint handler;
    @Nonnull
    public final Message message;
    @Nullable
    public final Throwable failureCause;

    public static HandlerExecutionRecord success(final ExecutionAction action,
            final Message thing, final JoinPoint handler) {
        return new HandlerExecutionRecord(action, handler, thing, null);
    }

    public static HandlerExecutionRecord failure(final ExecutionAction action,
            final Message thing, final JoinPoint handler,
            final Throwable failureCause) {
        return new HandlerExecutionRecord(action, handler, thing,
                unwrap(failureCause));
    }

    public String getCommandIdentifier() {
        return (String) message.getMetaData().get(DEFAULT_CORRELATION_KEY);
    }

    public boolean isCommand() {
        switch (action) {
        case dispatchCommandMessage:
        case handleCommandMessage:
        case handleCommand:
            return true;
        default:
            return false;
        }
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
        return Optional
                .ofNullable(isCommand() ? (CommandMessage<C>) message : null);
    }

    public <C> Optional<C> asCommand() {
        return this.<C>asCommandMessage().
                map(Message::getPayload);
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<EventMessage<E>> asEventMessage() {
        return Optional
                .ofNullable(isEvent() ? (EventMessage<E>) message : null);
    }

    public <C> Optional<C> asEvent() {
        return this.<C>asEventMessage().
                map(Message::getPayload);
    }

    private static Throwable unwrap(Throwable failureCause) {
        if (failureCause instanceof MessageHandlerInvocationException)
            failureCause = failureCause.getCause();
        if (failureCause instanceof InvocationTargetException)
            failureCause = failureCause.getCause();
        return failureCause;
    }

    public enum ExecutionAction {
        dispatchCommandMessage,
        handleCommandMessage,
        handleCommand,
        handleEventMessage,
        handleEvent
    }
}
