package hm.binkley.boxfuse;

import org.joda.money.BigMoney;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static hm.binkley.boxfuse.HelloWorldController.PATH;
import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(PATH)
public class HelloWorldController {
    public static final String PATH = "/hello-world";

    private static final String texanTemplate = "Howdy, %s!";
    private static final String russianTemplate = "Привет, %s!";
    private final AtomicLong counter = new AtomicLong();

    @ToggledFeature(true)
    @RequestMapping(value = "/{name}", method = GET)
    public Greeting sayHowdy(@PathVariable("name") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(texanTemplate, name), BigMoney.parse("USD 1.00"));
    }

    @ToggledFeature(false)
    @RequestMapping(value = "/{name}", method = GET)
    public Greeting sayPrivet(@PathVariable("name") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(russianTemplate, name), BigMoney.parse("USD 2.00"));
    }
}
