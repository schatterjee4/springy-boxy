package hm.binkley.man;

import org.axonframework.auditing.AuditDataProvider;
import org.axonframework.commandhandling.CommandMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * {@code CombiningAuditDataProvider} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
public class CombiningAuditDataProvider
        implements AuditDataProvider {
    private final List<AuditDataProvider> providers;

    public CombiningAuditDataProvider(final AuditDataProvider... providers) {
        this(asList(providers));
    }

    public CombiningAuditDataProvider(
            final List<AuditDataProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Map<String, Object> provideAuditDataFor(
            final CommandMessage<?> command) {
        final Map<String, Object> auditData = new LinkedHashMap<>();
        for (final AuditDataProvider provider : providers)
            auditData.putAll(provider.provideAuditDataFor(command));
        return auditData;
    }
}
