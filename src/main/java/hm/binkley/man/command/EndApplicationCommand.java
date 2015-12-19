package hm.binkley.man.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * {@code EndCommand} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation
 */
@Builder
@EqualsAndHashCode
@Getter
@ToString
public class EndApplicationCommand {
    @Nonnull
    @TargetAggregateIdentifier
    private final UUID id;
}
