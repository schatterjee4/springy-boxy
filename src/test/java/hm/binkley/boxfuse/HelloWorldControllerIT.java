package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static com.jayway.jsonpath.JsonPath.read;
import static hm.binkley.boxfuse.HelloWorldController.PATH;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest({"server.port:0", "management.port:0"})
public class HelloWorldControllerIT {
    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldGreet() {
        final String body = new RestTemplate().getForObject(
                format("http://localhost:%d/%s/{name}", port, PATH),
                String.class, "Brian");
        final int id = read(body, "$.id");
        assertThat(id).isEqualTo(1);
        final String content = read(body, "$.content");
        assertThat(content).isEqualTo("Howdy, Brian!");
    }
}
