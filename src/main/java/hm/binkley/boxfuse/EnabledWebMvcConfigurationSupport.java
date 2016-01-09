package hm.binkley.boxfuse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class EnabledWebMvcConfigurationSupport
        extends WebMvcConfigurationSupport {
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new EnabledRequestMappingHandlerMapping();
    }
}
