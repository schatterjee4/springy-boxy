package hm.binkley.boxfuse;

import org.springframework.stereotype.Component;

@Component
public class EnabledChecker {
    public boolean isMapped(final ToggledFeature toggledFeature) {
        return null == toggledFeature || toggledFeature.value();
    }
}
