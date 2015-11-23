package hm.binkley.man.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * {@code ApplicationController} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">Brian Oxley</a>
 * @todo Needs documentation
 */
@Controller
@EnableAutoConfiguration
public class ApplicationController {
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello, world!";
    }
}
