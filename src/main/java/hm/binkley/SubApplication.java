package hm.binkley;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("hm.binkley.subsub")
public class SubApplication {
    public static void main(final String... args) {
        new SpringApplicationBuilder().
                parent(SubParent.class).
                child(SubApplication.class).
                run(args);
    }

    public static class SubParent {
        @Bean
        public Foo foo() {
            return new Foo("Not Bob!");
        }
    }

    @RequiredArgsConstructor
    @ToString
    public static class Foo {
        private final String name;
    }
}
