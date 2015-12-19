package hm.binkley.man.aggregate;

import hm.binkley.man.audit.AuditRecord;
import hm.binkley.man.audit.ExecutionRecord;
import hm.binkley.man.audit.UnitOfWorkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code ConsistentRecords} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation
 */
@SuppressWarnings("CollectionDeclaredAsConcreteClass")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Component
public final class ConsistentRecords {
    @Nonnull
    private final ArrayList<ExecutionRecord> executionRecords;
    @Nonnull
    private final ArrayList<AuditRecord> auditRecords;
    @Nonnull
    private final ArrayList<UnitOfWorkRecord> unitOfWorkRecords;

    public void assertConsistency(final int commandCount) {
        assertThat(executionRecords).isNotEmpty();
        assertThat(auditRecords).isNotEmpty();
        assertThat(unitOfWorkRecords).isNotEmpty();
        final Set<Object> cids = concat(concat(executions(), records()),
                workUnits()).
                collect(toSet());
        assertThat(cids).hasSize(commandCount);

        assertThat(lastOf(executionRecords).failureCause).
                isSameAs(lastOf(auditRecords).failureCause);
        assertThat(lastOf(executionRecords).failureCause).
                isSameAs(lastOf(unitOfWorkRecords).failureCause);
    }

    private Stream<String> executions() {
        return executionRecords.stream().
                map(ExecutionRecord::getCommandIdentifier);
    }

    private Stream<String> records() {
        return auditRecords.stream().
                map(AuditRecord::getCommandIdentifier);
    }

    private Stream<String> workUnits() {
        return unitOfWorkRecords.stream().
                flatMap(UnitOfWorkRecord::getCommandIdentifiers);
    }

    private static <T> T lastOf(final List<T> list) {
        return list.get(list.size() - 1);
    }
}
