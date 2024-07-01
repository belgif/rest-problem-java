package io.github.belgif.rest.problem.spring;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.Problem;

@RestControllerAdvice
@Order(1)
public class NoResourceFoundExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Problem> handleNoResourceFoundException(
            NoResourceFoundException exception) {
        ResourceNotFoundProblem problem = new ResourceNotFoundProblem();
        problem.setDetail(String.format("No resource %s found",
                exception.getResourcePath().startsWith("/") ? exception.getResourcePath()
                        : "/" + exception.getResourcePath()));
        return ProblemMediaType.INSTANCE.toResponse(problem);
    }

}
