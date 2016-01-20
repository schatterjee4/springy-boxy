package hm.binkley.boxfuse;

import org.joda.money.BigMoney;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

@RestController
@RequestMapping("/hello-world")
public class HelloWorldController {
    private static final String texanTemplate = "Howdy, %s!";
    private static final String russianTemplate = "Привет, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Enabled(true)
    @RequestMapping("/greet/{name}")
    public Greeting sayHowdy(@PathVariable("name") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(texanTemplate, name), BigMoney.parse("USD 1.00"));
    }

    @Enabled(false)
    @RequestMapping("/greet/{name}")
    public Greeting sayPrivet(@PathVariable("name") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(russianTemplate, name), BigMoney.parse("USD 2.00"));
    }
}
