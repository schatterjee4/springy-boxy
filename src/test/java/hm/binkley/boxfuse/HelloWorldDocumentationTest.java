package hm.binkley.boxfuse;

import hm.binkley.Main;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static java.lang.System.getProperty;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest({"server.port:0", "management.port:0"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Main.class)
@WebAppConfiguration
public class HelloWorldDocumentationTest {
    /** @todo Maven provide the name for the location. */
    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation(
            getProperty("restdocs.snippetDirectory"));

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
    public void generateDocumentation()
            throws Exception {
        mockMvc.perform(get("/hello-world/Brian").
                accept(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("hello-world"));
    }
}
