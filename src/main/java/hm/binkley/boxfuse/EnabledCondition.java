package hm.binkley.boxfuse;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

public class EnabledCondition
        extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(final ConditionContext context,
            final AnnotatedTypeMetadata metadata) {
        final boolean enabled = (boolean) metadata.
                getAnnotationAttributes(ToggledFeature.class.getName()).
                get("value");
        return enabled ? match() : noMatch("You're not enabled, Bob");
    }
}
