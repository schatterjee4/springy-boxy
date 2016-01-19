package hm.binkley.boxfuse;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Conditional(EnabledCondition.class)
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ToggledFeature {
    boolean value();
}
