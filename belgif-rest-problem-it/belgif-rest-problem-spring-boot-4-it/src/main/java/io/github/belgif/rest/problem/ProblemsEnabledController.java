package io.github.belgif.rest.problem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.belgif.rest.problem.config.EnableProblems;

@EnableProblems
@RestController
@RequestMapping("/enabled")
public class ProblemsEnabledController {

    @GetMapping("/runtime")
    public void runtime() {
        throw new RuntimeException("oops");
    }

}
