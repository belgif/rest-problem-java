package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.HttpResponseHeaders;
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
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_token\", error_description=\"The access token is invalid\"");
    }

    @Test
    void realm() {
        InvalidAccessTokenProblem problem = new InvalidAccessTokenProblem().realm("test");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"test\", error=\"invalid_token\", error_description=\"The access token is invalid\"");
    }

    @Test
    void customReason() {
        InvalidAccessTokenProblem problem = new InvalidAccessTokenProblem("Custom reason");
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:invalidAccessToken");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/invalidAccessToken.html");
        assertThat(problem.getTitle()).isEqualTo("Invalid Access Token");
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail())
                .isEqualTo("The Bearer access token found in the Authorization HTTP header is invalid: Custom reason");
        assertThat(problem.getHttpResponseHeaders()).containsEntry(HttpResponseHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_token\", error_description=\"Custom reason\"");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(InvalidAccessTokenProblem.class).hasAnnotation(ProblemType.class);
        assertThat(InvalidAccessTokenProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:invalidAccessToken");
    }

    @Test
    void equalsAndHashCode() {
        InvalidAccessTokenProblem problem = new InvalidAccessTokenProblem("A");
        InvalidAccessTokenProblem equal = new InvalidAccessTokenProblem("A");
        InvalidAccessTokenProblem other = new InvalidAccessTokenProblem("B");
        InvalidAccessTokenProblem otherBis = new InvalidAccessTokenProblem();
        otherBis.setDetail("other");

        assertThat(problem).isEqualTo(problem);
        assertThat(problem).hasSameHashCodeAs(problem);
        assertThat(problem).isEqualTo(equal);
        assertThat(problem).hasSameHashCodeAs(equal);
        assertThat(problem).hasToString(equal.toString());
        assertThat(problem).isNotEqualTo(other);
        assertThat(problem).isNotEqualTo(otherBis);
        assertThat(problem).doesNotHaveSameHashCodeAs(other);
        assertThat(problem).isNotEqualTo("other type");
    }

}
