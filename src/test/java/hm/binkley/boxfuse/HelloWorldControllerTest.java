package hm.binkley.boxfuse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders
        .standaloneSetup;

@Ignore("TODO: how to mock mvc and still use @Enabled?")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
public class HelloWorldControllerTest {
    private MockMvc mvc;

    @Before
    public void setUp()
            throws Exception {
        mvc = standaloneSetup(new HelloWorldController()).
                build();
    }

    @Test
    public void getHello()
            throws Exception {
        mvc.perform(get(format("%s/%s", "/hello-world", "Brian")).
                accept(APPLICATION_JSON_UTF8)).
                andExpect(status().isOk()).
                andExpect(content().
                        json("{\"id\":1,\"content\":\"Hello, Brian!\","
                                + "\"value\":{\"currency\":\"USD\","
                                + "\"amount\":1.00}}"));
    }
}
