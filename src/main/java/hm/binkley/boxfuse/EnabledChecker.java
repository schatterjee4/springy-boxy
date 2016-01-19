package hm.binkley.boxfuse;

import org.springframework.stereotype.Component;

@Component
public class EnabledChecker {
    public boolean isMapped(final Enabled enabled) {
        return null == enabled || enabled.value();
    }
}
