package hm.binkley.boxfuse;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation
        .RequestMappingHandlerMapping;

@Configuration
public class EnabledWebMvcConfigurationSupport
        extends WebMvcConfigurationSupport {
    private static final EnabledRequestMappingHandlerMapping enabledMapping
            = new EnabledRequestMappingHandlerMapping();

    @Bean
    @Override
    @Primary
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return super.requestMappingHandlerMapping();
    }

    @Override
    protected EnabledRequestMappingHandlerMapping
    createRequestMappingHandlerMapping() {
        return enabledMapping;
    }

    @Configuration
    public static class ForceWebMvcAutoConfiguration
            extends WebMvcAutoConfiguration {}
}
