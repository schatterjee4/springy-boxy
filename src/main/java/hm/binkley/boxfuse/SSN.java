package hm.binkley.boxfuse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
@ToString
@Getter
@EqualsAndHashCode
public class SSN {
    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;
    private String left;
    private String right;

//    @ElementCollection(targetClass = String.class)
//    @CollectionTable(joinColumns = @JoinColumn(name = "id"))
//    @Column(name = "account_id")
//    private Set<String> accountIds = new HashSet<>();

    protected SSN() {}

    public SSN(final String left, final String right) {
        this.left = left;
        this.right = right;
    }
}
