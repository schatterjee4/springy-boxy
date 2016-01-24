package hm.binkley.other;

import hm.binkley.other.OtherConfiguration.OtherProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    public Qux qux() {
        return new Qux(properties.getThing());
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Qux {
        private final String name;
    }

    @ConfigurationProperties("other")
    @Getter
    @Setter
    @ToString
    public static class OtherProperties {
        private String thing;
        private int count;
    }
}
