package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.converter.json
        .MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest({"server.port:0", "management.port:0"})
public class HelloWorldControllerIT {
    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;
    private RestTemplate rest;

    @Before
    public void setUpRest() {
        rest = new RestTemplate(singletonList(converter));
    }

    @Test
    public void shouldGreet() {
        final Greeting greeting = rest.getForObject(
                format("http://localhost:%d/hello-world/greet/{name}", port),
                Greeting.class, "Brian");

        assertThat(greeting.getContent()).isEqualTo("Howdy, Brian!");
        assertThat(greeting.getValue()).isEqualTo(BigMoney.parse("USD 1.00"));
    }
}
