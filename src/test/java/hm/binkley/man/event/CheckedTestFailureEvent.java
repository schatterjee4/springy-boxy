package hm.binkley.man.event;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class CheckedTestFailureEvent {
    public final UUID id;
}
