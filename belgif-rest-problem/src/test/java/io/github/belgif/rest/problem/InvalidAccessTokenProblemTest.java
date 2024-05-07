package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class InvalidAccessTokenProblemTest {

    @Test
    void construct() {
        InvalidAccessTokenProblem problem = new InvalidAccessTokenProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:invalidAccessToken");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/invalidAccessToken.html");
        assertThat(problem.getTitle()).isEqualTo("Invalid Access Token");
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail())
                .isEqualTo("The Bearer access token found in the Authorization HTTP header is invalid");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(InvalidAccessTokenProblem.class).hasAnnotation(ProblemType.class);
        assertThat(InvalidAccessTokenProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:invalidAccessToken");
    }

}
