package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.converter.json
        .MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.hypermedia
        .HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
        .document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
        .documentationConfiguration;
import static org.springframework.restdocs.mockmvc
        .RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders
        .webAppContextSetup;

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

    /** @todo Maven provide the name for the location. */
    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation(
            "target/generated-snippets");
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUpDocumentation() {
        mockMvc = webAppContextSetup(context).
                apply(documentationConfiguration(restDocumentation)).
                build();
    }

    @Test
    public void shouldGreet() {
        final Greeting greeting = rest.getForObject(
                format("http://localhost:%d/hello-world/greet/{name}", port),
                Greeting.class, "Brian");

        assertThat(greeting.getContent()).isEqualTo("Howdy, Brian!");
        assertThat(greeting.getValue()).isEqualTo(BigMoney.parse("USD 1.00"));
    }

    @Test
    public void generateDocumentation()
            throws Exception {
        mockMvc.perform(get("/hello-world/greet/Brian").
                accept(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("hello-world", links()));
    }
}
