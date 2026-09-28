package io.github.belgif.rest.problem.it;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.it.model.JacksonModel;
import io.github.belgif.rest.problem.spring.ProblemMediaType;

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

    @GetMapping("/inconsistentProblemStatus")
    public ResponseEntity<String> inconsistentProblemStatus() {
        return ResponseEntity.status(402) // -> HTTP status code 402 instead of 400
                .contentType(ProblemMediaType.INSTANCE)
                .body(ProblemPayloads.INCONSISTENT_STATUS_CODE); // -> problem status code 401 instead of 400
    }

    @GetMapping("/healthDown")
    public ResponseEntity<Map<String, String>> healthDown() {
        return ResponseEntity
                .status(503)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "DOWN"));
    }

}
