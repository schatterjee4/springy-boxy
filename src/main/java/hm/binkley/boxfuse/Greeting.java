package hm.binkley.boxfuse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class Greeting {
    private final long id;
    private final String content;
}
