package hm.binkley.man.aggregate;

import hm.binkley.man.command.TestSuccessCommand;
import hm.binkley.man.event.TestSuccessEvent;
import org.axonframework.test.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.axonframework.test.Fixtures.newGivenWhenThenFixture;

public class TestSuccessAggregateTest {
    private FixtureConfiguration<TestSuccessAggregate> fixture;

    @Before
    public void setUpFixture() {
        fixture = newGivenWhenThenFixture(TestSuccessAggregate.class);
    }

    @Test
    public void shouldTestSuccess() {
        final UUID id = randomUUID();
        fixture.given().
                when(new TestSuccessCommand(id)).
                expectEvents(new TestSuccessEvent(id));
    }
}
