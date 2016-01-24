package hm.binkley.other;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Conditional(OtherCondition.class)
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ConditionalOnOther {}
