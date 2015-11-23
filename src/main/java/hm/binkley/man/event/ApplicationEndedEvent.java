package hm.binkley.man.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

/**
 * {@code ApplicationEndedEvent} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Builder
@EqualsAndHashCode
@Getter
@ToString
public class ApplicationEndedEvent {
    @Nonnull
    private final String id;
}
