package hm.binkley.man.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * {@code StartCommand} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Builder
@EqualsAndHashCode
@Getter
@ToString
public class StartApplicationCommand {
    @Nonnull
    @TargetAggregateIdentifier
    private final UUID id;
}
