package hm.binkley.man.aggregate;

import hm.binkley.man.command.EndApplicationCommand;
import hm.binkley.man.command.StartApplicationCommand;
import hm.binkley.man.event.ApplicationEndedEvent;
import hm.binkley.man.event.ApplicationStartedEvent;
import org.axonframework.test.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.axonframework.test.Fixtures.newGivenWhenThenFixture;

public class ApplicationTest {
    private FixtureConfiguration<Application> fixture;

    private StartApplicationCommand startApplication;
    private ApplicationStartedEvent applicationStarted;
    private ApplicationEndedEvent applicationEnded;
    private EndApplicationCommand endApplication;

    @Before
    public void setUpFixture() {
        fixture = newGivenWhenThenFixture(Application.class);
    }

    @Before
    public void setUpAxon() {
        final UUID id = randomUUID();
        startApplication = StartApplicationCommand.builder().
                id(id).
                build();
        applicationStarted = ApplicationStartedEvent.builder().
                id(id).
                build();
        endApplication = EndApplicationCommand.builder().
                id(id).
                build();
        applicationEnded = ApplicationEndedEvent.builder().
                id(id).
                build();
    }

    @Test
    public void shouldStartApplication() {
        fixture.given().
                when(startApplication).
                expectEvents(applicationStarted);
    }

    @Test
    public void shouldEndApplication() {
        fixture.given(applicationStarted).
                when(endApplication).
                expectEvents(applicationEnded);
    }
}
