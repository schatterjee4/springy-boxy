package hm.binkley.boxfuse;

import org.springframework.context.annotation.Bean;

//@Configuration
public class FooConfiguration {
    @Bean
    @ToggledFeature(true)
    public Foo foo() {
        return new Foo();
    }

    @Bean
    @ToggledFeature(false)
    public Foo fooToo() {
        return new Foo();
    }

    public static class Foo {}
}
