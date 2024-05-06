package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class PayloadTooLargeProblemTest {

    @Test
    void construct() {
        PayloadTooLargeProblem problem = new PayloadTooLargeProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:payloadTooLarge");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/payloadTooLarge.html");
        assertThat(problem.getTitle()).isEqualTo("Payload Too Large");
        assertThat(problem.getStatus()).isEqualTo(413);
    }

    @Test
    void constructWithLimit() {
        PayloadTooLargeProblem problem = new PayloadTooLargeProblem(10L);
        assertThat(problem.getLimit()).isEqualTo(10L);
    }

    @Test
    void limit() {
        PayloadTooLargeProblem problem = new PayloadTooLargeProblem();
        problem.setLimit(10L);
        assertThat(problem.getLimit()).isEqualTo(10L);
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(PayloadTooLargeProblem.class).hasAnnotation(ProblemType.class);
        assertThat(PayloadTooLargeProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:payloadTooLarge");
    }

    @Test
    void equalsAndHashCode() {
        PayloadTooLargeProblem problem = new PayloadTooLargeProblem(10L);
        PayloadTooLargeProblem equal = new PayloadTooLargeProblem(10L);
        PayloadTooLargeProblem other = new PayloadTooLargeProblem(99L);
        PayloadTooLargeProblem otherBis = new PayloadTooLargeProblem();
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
