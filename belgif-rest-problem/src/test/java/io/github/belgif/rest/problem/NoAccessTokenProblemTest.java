package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

class NoAccessTokenProblemTest {

    @Test
    void construct() {
        NoAccessTokenProblem problem = new NoAccessTokenProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:noAccessToken");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/noAccessToken.html");
        assertThat(problem.getTitle()).isEqualTo("No Access Token");
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail())
                .isEqualTo("No Bearer access token found in Authorization HTTP header");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE, "Bearer");
    }

    @Test
    void constructWithRealm() {
        NoAccessTokenProblem problem = new NoAccessTokenProblem("test");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"test\"");
    }

    @Test
    void realm() {
        NoAccessTokenProblem problem = new NoAccessTokenProblem().realm("test");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"test\"");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(NoAccessTokenProblem.class).hasAnnotation(ProblemType.class);
        assertThat(NoAccessTokenProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:noAccessToken");
    }

}
