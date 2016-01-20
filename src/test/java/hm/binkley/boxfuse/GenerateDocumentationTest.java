package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

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

@Ignore("TODO: Fix doc generation for JPA endpoint")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest({"server.port:0", "management.port:0"})
public class GenerateDocumentationTest {
    /** @todo Maven provide the name for the location. */
    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation(
            "target/generated-snippets");

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).
                apply(documentationConfiguration(restDocumentation)).
                build();
    }

    @Test
    public void helloWorld()
            throws Exception {
        mockMvc.perform(get("/hello-world/greet/Brian").
                accept(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("hello-world", links()));
    }

    @Test
    public void jpa()
            throws Exception {
        mockMvc.perform(get("/jpa/ssns").
                accept(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("jpa", links()));
    }
}
