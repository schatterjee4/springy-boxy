package hm.binkley.man.controller;

import hm.binkley.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code ApplicationControllerIT} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0", "management.port=0"})
public class ApplicationControllerIT {
    @Value("${local.server.port}")
    private int port;

    private URL base;
    private RestTemplate template;

    @Before
    public void setUp()
            throws Exception {
        base = new URL(format("http://localhost:%d/", port));
        template = new TestRestTemplate();
    }

    @Test
    public void getHello()
            throws Exception {
        final ResponseEntity<String> response = template
                .getForEntity(base.toURI(), String.class);

        assertThat(response.getBody()).
                isEqualTo("Hello, world!");
    }
}
