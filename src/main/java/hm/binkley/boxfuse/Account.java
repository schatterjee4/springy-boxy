package hm.binkley.boxfuse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;

@Entity
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = AUTO)
    private long accountId;
    private String name;

    public Account(final String name) {
        this.name = name;
    }
}
