package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

class ExpiredAccessTokenProblemTest {

    @Test
    void construct() {
        ExpiredAccessTokenProblem problem = new ExpiredAccessTokenProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:expiredAccessToken");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/expiredAccessToken.html");
        assertThat(problem.getTitle()).isEqualTo("Expired Access Token");
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail())
                .isEqualTo("The Bearer access token found in the Authorization HTTP header has expired");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_token\", error_description=\"The access token expired\"");
    }

    @Test
    void constructWithRealm() {
        ExpiredAccessTokenProblem problem = new ExpiredAccessTokenProblem("test");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"test\", error=\"invalid_token\", error_description=\"The access token expired\"");
    }

    @Test
    void realm() {
        ExpiredAccessTokenProblem problem = new ExpiredAccessTokenProblem().realm("test");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"test\", error=\"invalid_token\", error_description=\"The access token expired\"");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(ExpiredAccessTokenProblem.class).hasAnnotation(ProblemType.class);
        assertThat(ExpiredAccessTokenProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:expiredAccessToken");
    }

}
