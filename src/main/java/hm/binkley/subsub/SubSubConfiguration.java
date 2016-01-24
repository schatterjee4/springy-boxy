package hm.binkley.subsub;

import hm.binkley.SubApplication.Foo;
import hm.binkley.other.ConditionalOnOther;
import hm.binkley.other.OtherConfiguration.Qux;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubSubConfiguration {
    @Bean
    @ConditionalOnOther
    public Bar bar(final Foo foo, final Qux qux) {
        return new Bar("Totally not Bar!", foo, qux);
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Bar {
        private final String name;
        private final Foo foo;
        private final Qux qux;

        public String quxName() {
            return qux.getName();
        }
    }
}
