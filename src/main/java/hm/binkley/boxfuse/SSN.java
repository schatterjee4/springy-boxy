package hm.binkley.boxfuse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;

@Entity
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class SSN {
    @Id
    @GeneratedValue(strategy = AUTO)
    private long ssnId;
    private String left;
    private String right;

    @OneToMany(fetch = EAGER, cascade = MERGE)
    private List<Account> accounts;

    public SSN(final String left, final String right,
            final Account... accounts) {
        this(left, right, asList(accounts));
    }

    public SSN(final String left, final String right,
            final List<Account> accounts) {
        this.left = left;
        this.right = right;
        this.accounts = accounts;
    }
}
