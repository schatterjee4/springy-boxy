package hm.binkley.boxfuse;

import hm.binkley.Application;
import org.junit.Before;
import org.junit.Ignore;
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

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
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
public class JpaControllerIT {
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private MappingJackson2HttpMessageConverter converter;
    private RestTemplate rest;

    @Before
    public void setUpRest() {
        rest = new RestTemplate(singletonList(converter));
    }

    @Autowired
    private AccountRepository accounts;
    @Autowired
    private SSNRepository ssns;
    private List<SSN> savedSSNs;

    @Before
    public void setUpDatabase() {
        final Account bob = new Account("Bob");
        final Account mary = new Account("Mary");
        final List<Account> savedAccounts = accounts.save(asList(bob, mary));
        final SSN abc123 = new SSN("abc", "123", savedAccounts);
        final SSN pqr987 = new SSN("pqr", "987");
        // TODO: How to upsert?
        savedSSNs = ssns.findAll();
        if (savedSSNs.isEmpty())
            savedSSNs = ssns.save(asList(abc123, pqr987));
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
    public void shouldFetchSSNs() {
        final List<SSN> fetchedSSNs = asList(rest.getForObject(
                format("http://localhost:%d/jpa/ssns", port), SSN[].class));
        fetchedSSNs.forEach(System.out::println);
        assertThat(fetchedSSNs).isEqualTo(savedSSNs);
    }

    @Ignore("TODO: JSON parsing breaks here - why?")
    @Test
    public void generateDocumentation()
            throws Exception {
        mockMvc.perform(get("/jpa/ssns").
                accept(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("jpa", links()));
    }
}
