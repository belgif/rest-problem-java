package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class TooManyFailedRequestsProblemTest {

    @Test
    void construct() {
        TooManyFailedRequestsProblem problem = new TooManyFailedRequestsProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:tooManyFailedRequests");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/tooManyFailedRequests.html");
        assertThat(problem.getTitle()).isEqualTo("Too Many Failed Requests");
        assertThat(problem.getStatus()).isEqualTo(429);
    }

    @Test
    void limit() {
        TooManyFailedRequestsProblem problem = new TooManyFailedRequestsProblem();
        problem.setLimit(10L);
        assertThat(problem.getLimit()).isEqualTo(10L);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(TooManyFailedRequestsProblem.class).hasAnnotation(ProblemType.class);
        assertThat(TooManyFailedRequestsProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:tooManyFailedRequests");
    }

    @Test
    void equalsAndHashCode() {
        TooManyFailedRequestsProblem problem = new TooManyFailedRequestsProblem();
        problem.setLimit(10L);
        TooManyFailedRequestsProblem equal = new TooManyFailedRequestsProblem();
        equal.setLimit(10L);
        TooManyFailedRequestsProblem other = new TooManyFailedRequestsProblem();
        other.setLimit(99L);
        TooManyFailedRequestsProblem otherBis = new TooManyFailedRequestsProblem();
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
