package hm.binkley.man;

import org.axonframework.auditing.AuditDataProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class CombiningAuditDataProviderTest {
    @Test
    public void shouldDelegateAll() {
        final AuditDataProvider first = command -> singletonMap("a", 1);
        final AuditDataProvider second = command -> singletonMap("b", 2);
        final AuditDataProvider combining = new CombiningAuditDataProvider(
                first, second);

        assertThat(combining.provideAuditDataFor(null)).
                isEqualTo(new HashMap<String, Object>() {{
                    put("a", 1);
                    put("b", 2);
                }});
    }

    @Test
    public void shouldDelegateInOrder() {
        final AuditDataProvider first = command -> singletonMap("a", 1);
        final AuditDataProvider second = command -> singletonMap("b", 2);
        final AuditDataProvider combining = new CombiningAuditDataProvider(
                first, second);

        final Map<String, Object> auditData = combining.
                provideAuditDataFor(null);
        final Iterator<String> keys = auditData.keySet().iterator();
        assertThat(keys.hasNext()).isTrue();
        assertThat(keys.next()).isEqualTo("a");
        assertThat(keys.hasNext()).isTrue();
        assertThat(keys.next()).isEqualTo("b");
    }
}
