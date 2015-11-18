package hm.binkley.boxfuse;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.when;
import static hm.binkley.boxfuse.HelloWorldController.PATH;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HelloWorldConfiguration.class)
@IntegrationTest({"server.port:0", "management.port:0"})
@WebAppConfiguration
public class HelloWorldControllerIntegrationTest {
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void shouldGreet() {
        when().get(format("%s/{name}", PATH), "Brian").
                then().statusCode(SC_OK).
                body("id", equalTo(1)).
                body("content", equalTo("Hello, Brian!"));
    }
}
