package hm.binkley.boxfuse;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class EnabledRequestMappingHandlerMapping
        extends RequestMappingHandlerMapping {
    @Override
    protected void detectHandlerMethods(final Object handler) {
        super.detectHandlerMethods(handler);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(final Method method,
            final Class<?> handlerType) {
        final Enabled enabled = findAnnotation(method, Enabled.class);
        final boolean mapped = null == enabled || enabled.value();
        return mapped ? super.getMappingForMethod(method, handlerType) : null;
    }
}
