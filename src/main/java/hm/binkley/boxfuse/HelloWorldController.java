package hm.binkley.boxfuse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

@Controller
@RequestMapping("/hello-world")
public class HelloWorldController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Greeting sayHello(@RequestParam(value = "name", required = false,
            defaultValue = "Stranger") final String name) {
        return new Greeting(counter.incrementAndGet(),
                format(template, name));
    }
}
