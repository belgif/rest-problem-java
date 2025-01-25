package io.github.belgif.rest.problem;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.it.model.JacksonModel;

@RestController
@RequestMapping("/backend")
public class BackendController {

    @GetMapping("/ok")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("OK");
    }

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

    @GetMapping("/applicationJsonProblem")
    public ResponseEntity<BadRequestProblem> applicationJsonProblem() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request with application/json media type from backend");
        return ResponseEntity.badRequest().body(problem);
    }

    @GetMapping("/jacksonMismatchedInput")
    public ResponseEntity<JacksonModel> mismatchedInput() {
        JacksonModel model = new JacksonModel(null);
        model.setDescription("description");
        return ResponseEntity.ok(model);
    }

}
