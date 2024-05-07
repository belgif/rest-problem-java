package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class InternalServerErrorProblemTest {

    @Test
    void construct() {
        InternalServerErrorProblem problem = new InternalServerErrorProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:internalServerError");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/internalServerError.html");
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getStatus()).isEqualTo(500);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(InternalServerErrorProblem.class).hasAnnotation(ProblemType.class);
        assertThat(InternalServerErrorProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:internalServerError");
    }

}
