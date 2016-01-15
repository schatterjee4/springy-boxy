package hm.binkley.boxfuse;

import hm.binkley.boxfuse.FooConfiguration.Foo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(FooConfiguration.class)
public class FooConfigurationIT {
    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    public void thereCanBeOnlyOne() {
        assertThat(context.getBeanNamesForType(Foo.class)).hasSize(1);
    }
}
