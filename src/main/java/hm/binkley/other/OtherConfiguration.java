package hm.binkley.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties
        .EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OtherProperties.class)
public class OtherConfiguration {
    @Autowired
    private OtherProperties properties;

    @Bean
    public OtherProperties otherProperties() {
        return properties;
    }

    @Bean
    public Qux qux() {
        return new Qux(properties.getThing());
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Qux {
        private final String name;
    }
}
