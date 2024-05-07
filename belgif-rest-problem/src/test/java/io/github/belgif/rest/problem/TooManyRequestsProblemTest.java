package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class TooManyRequestsProblemTest {

    @Test
    void construct() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:tooManyRequests");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/tooManyRequests.html");
        assertThat(problem.getTitle()).isEqualTo("Too Many Requests");
        assertThat(problem.getStatus()).isEqualTo(429);
    }

    @Test
    void limit() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setLimit(10L);
        assertThat(problem.getLimit()).isEqualTo(10L);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(TooManyRequestsProblem.class).hasAnnotation(ProblemType.class);
        assertThat(TooManyRequestsProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:tooManyRequests");
    }

    @Test
    void equalsAndHashCode() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setLimit(10L);
        TooManyRequestsProblem equal = new TooManyRequestsProblem();
        equal.setLimit(10L);
        TooManyRequestsProblem other = new TooManyRequestsProblem();
        other.setLimit(99L);
        TooManyRequestsProblem otherBis = new TooManyRequestsProblem();
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
