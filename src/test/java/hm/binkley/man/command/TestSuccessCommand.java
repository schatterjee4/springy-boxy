package hm.binkley.man.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.annotation.Nonnull;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class TestSuccessCommand {
    @Nonnull
    @TargetAggregateIdentifier
    private final UUID id;
}
