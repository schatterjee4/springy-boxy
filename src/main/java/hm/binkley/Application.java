package hm.binkley;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@SpringBootApplication
public class Application {
    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @EnableWebSecurity
    public static class Anonymous extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http)
                throws Exception {
            http.authorizeRequests().antMatchers("/**").permitAll();
        }
    }
}
