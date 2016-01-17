package hm.binkley.boxfuse;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Greeting {
    private long id;
    @Nonnull
    private String content;

    public Greeting() {}
}
