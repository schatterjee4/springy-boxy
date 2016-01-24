package hm.binkley.other;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties
        .ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.springframework.boot.autoconfigure.condition
        .ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition
        .ConditionOutcome.noMatch;
import static org.springframework.context.annotation.ConfigurationCondition
        .ConfigurationPhase.REGISTER_BEAN;

public class OtherCondition
        extends SpringBootCondition
        implements ConfigurationCondition {
    @Override
    public ConditionOutcome getMatchOutcome(final ConditionContext context,
            final AnnotatedTypeMetadata metadata) {
        final OtherProperties properties = context.
                getBeanFactory().
                getBean("other.CONFIGURATION_PROPERTIES",
                        OtherProperties.class);
        context.getBeanFactory()
                .getBean(ConfigurationPropertiesBindingPostProcessor.class)
                .postProcessBeforeInitialization(properties,
                        "other.CONFIGURATION_PROPERTIES");
        return 1 < properties.getCount() ? match()
                : noMatch("Less than 2 things: " + properties);
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return REGISTER_BEAN;
    }
}
