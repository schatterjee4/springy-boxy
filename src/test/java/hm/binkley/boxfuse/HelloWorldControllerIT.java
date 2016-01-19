package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.joda.money.BigMoney;
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

import java.util.List;

import static hm.binkley.boxfuse.HelloWorldController.FIRST_SSN;
import static hm.binkley.boxfuse.HelloWorldController.PATH;
import static hm.binkley.boxfuse.HelloWorldController.SECOND_SSN;
import static java.lang.String.format;
import static java.util.Arrays.asList;
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

    /** @todo Why do I need to provide the converter? */
    @Test
    public void shouldGreet() {
        final Greeting greeting = new RestTemplate(singletonList(converter)).
                getForObject(
                        format("http://localhost:%d/%s/greet/{name}", port,
                                PATH), Greeting.class, "Brian");

        assertThat(greeting.getId()).isEqualTo(1);
        assertThat(greeting.getContent()).isEqualTo("Howdy, Brian!");
        assertThat(greeting.getValue()).isEqualTo(BigMoney.parse("USD 1.00"));
    }

    @Test
    public void shouldFetchSSNs() {
        final List<SSN> ssns = asList(
                new RestTemplate(singletonList(converter)).getForObject(
                        format("http://localhost:%d/%s/ssns", port, PATH),
                        SSN[].class));

        assertThat(ssns).containsExactly(FIRST_SSN, SECOND_SSN);
    }
}
