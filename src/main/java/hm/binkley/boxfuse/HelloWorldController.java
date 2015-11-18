package hm.binkley.boxfuse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static hm.binkley.boxfuse.HelloWorldController.PATH;
import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(PATH)
public class HelloWorldController {
    public static final String PATH = "/hello-world";

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @ResponseBody
    @RequestMapping(value = "/{name}", method = GET)
    public Greeting sayHello(@PathVariable("name") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(template, name));
    }
}
