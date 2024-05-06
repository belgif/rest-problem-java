package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class ServiceUnavailableProblemTest {

    @Test
    void construct() {
        ServiceUnavailableProblem problem = new ServiceUnavailableProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:serviceUnavailable");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/serviceUnavailable.html");
        assertThat(problem.getTitle()).isEqualTo("Service is unavailable");
        assertThat(problem.getStatus()).isEqualTo(503);
    }

    @Test
    void constructWithCause() {
        Throwable cause = new IllegalStateException("cause");
        ServiceUnavailableProblem problem = new ServiceUnavailableProblem(cause);
        assertThat(problem).hasCause(cause);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(ServiceUnavailableProblem.class).hasAnnotation(ProblemType.class);
        assertThat(ServiceUnavailableProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:serviceUnavailable");
    }

}
