package io.github.belgif.rest.problem.spring;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.Problem;

/**
 * The "application/problem+json" MediaType.
 */
public class ProblemMediaType extends MediaType {

    public static final ProblemMediaType INSTANCE = new ProblemMediaType();

    private ProblemMediaType() {
        super("application", "problem+json");
    }

    public ResponseEntity<Problem> toResponse(Problem problem) {
        HttpHeaders headers = new HttpHeaders();
        if (problem instanceof HttpResponseHeaders responseHeaders) {
            responseHeaders.getHttpResponseHeaders()
                    .forEach((key, value) -> headers.add(key, String.valueOf(value)));
        }
        return ResponseEntity.status(problem.getStatus())
                .headers(headers)
                .contentType(ProblemMediaType.INSTANCE)
                .body(problem);
    }

}
