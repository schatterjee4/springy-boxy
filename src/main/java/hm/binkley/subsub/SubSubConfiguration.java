package hm.binkley.subsub;

import hm.binkley.SubApplication.Foo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubSubConfiguration {
    @Bean
    public Bar bar(final Foo foo) {
        return new Bar("Totally not Bar!", foo);
    }

    @RequiredArgsConstructor
    @ToString
    public static class Bar {
        private final String name;
        private final Foo foo;
    }
}
