package hm.binkley;

import hm.binkley.subsub.SubSubConfiguration.Bar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = SubApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SubApplicationIT {
    @Autowired
    private Bar bar;

    @Test
    public void testy() {
        assertThat(bar).isNotNull();
    }
}
