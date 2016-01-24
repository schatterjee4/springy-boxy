package hm.binkley.other;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("other")
@Getter
@Setter
@ToString
public class OtherProperties {
    private String thing;
    private int count;
}
