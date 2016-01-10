package hm.binkley.boxfuse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class EnabledWebMvcConfigurationSupport
        extends WebMvcConfigurationSupport {
    private static final EnabledRequestMappingHandlerMapping enabledMapping
            = new EnabledRequestMappingHandlerMapping();

    @Override
    protected EnabledRequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return enabledMapping;
    }
}
