package hm.binkley.boxfuse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


import javax.annotation.Nullable;
import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class EnabledRequestMappingHandlerMapping
        extends RequestMappingHandlerMapping {
    @Autowired
    private EnabledChecker checker;

    @Override
    protected RequestMappingInfo getMappingForMethod(final Method method,
            final Class<?> handlerType) {
        final ToggledFeature toggledFeature = findAnnotation(method, ToggledFeature.class);
        final boolean mapped = checker.isMapped(toggledFeature);
        return mapped ? super.getMappingForMethod(method, handlerType) :
                ignoreThisEndpoint();
    }

    @Nullable
    private RequestMappingInfo ignoreThisEndpoint() {return null;}
}
