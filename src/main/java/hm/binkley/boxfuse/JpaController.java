package hm.binkley.boxfuse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jpa")
public class JpaController {
    @Autowired
    private SSNRepository ssns;

    @RequestMapping("/ssns")
    public List<SSN> fetchSSNs() {
        return ssns.findAll();
    }
}
