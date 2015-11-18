package hm.binkley.boxfuse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class Greeting {
    private final long id;
    @Nonnull
    private final String content;
}
