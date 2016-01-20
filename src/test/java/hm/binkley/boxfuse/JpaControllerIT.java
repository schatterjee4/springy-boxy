package hm.binkley.boxfuse;

import hm.binkley.Application;
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

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void shouldFetchSSNs() {
        final Account bob = new Account("Bob");
        final Account mary = new Account("Mary");
        final List<Account> savedAccounts = accounts.save(asList(bob, mary));
        final SSN abc123 = new SSN("abc", "123", savedAccounts);
        final SSN pqr987 = new SSN("pqr", "987");
        final List<SSN> savedSSNs = ssns.save(asList(abc123, pqr987));

        final List<SSN> fetchedSSNs = asList(rest.getForObject(
                format("http://localhost:%d/jpa/ssns", port), SSN[].class));

        assertThat(fetchedSSNs).isEqualTo(savedSSNs);
    }
}
