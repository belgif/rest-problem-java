package io.github.belgif.rest.problem;

import java.net.URI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;

@RestController
@RequestMapping("/backend")
public class BackendController {

    @GetMapping("/badRequest")
    public void badRequest() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request from backend");
        throw problem;
    }

    @GetMapping("/custom")
    public void custom() {
        throw new CustomProblem("value from backend");
    }

    @GetMapping("/unmapped")
    public void unmapped() {
        Problem unmapped = new Problem(URI.create("urn:problem-type:belgif:test:unmapped"), "Unmapped problem", 400) {
        };
        unmapped.setDetail("Unmapped problem from backend");
        throw unmapped;
    }

}
