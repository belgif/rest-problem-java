package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class BadGatewayProblemTest {

    @Test
    void construct() {
        BadGatewayProblem problem = new BadGatewayProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:badGateway");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/badGateway.html");
        assertThat(problem.getTitle()).isEqualTo("Bad Gateway");
        assertThat(problem.getStatus()).isEqualTo(502);
        assertThat(problem.getDetail()).isEqualTo("Error in communication with upstream service");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(BadGatewayProblem.class).hasAnnotation(ProblemType.class);
        assertThat(BadGatewayProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:badGateway");
    }

}
