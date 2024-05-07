package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.ProblemType;

class MissingScopeProblemTest {

    @Test
    void construct() {
        MissingScopeProblem problem = new MissingScopeProblem();
        assertThat(problem.getType()).hasToString("urn:problem-type:belgif:missingScope");
        assertThat(problem.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/problems/missingScope.html");
        assertThat(problem.getTitle()).isEqualTo("Missing Scope");
        assertThat(problem.getStatus()).isEqualTo(403);
    }

    @Test
    void constructWithRequiredScopes() {
        MissingScopeProblem problem = new MissingScopeProblem("required-scope");
        assertThat(problem.getRequiredScopes()).isUnmodifiable().containsExactly("required-scope");
    }

    @Test
    void requiredScopes() {
        MissingScopeProblem problem = new MissingScopeProblem();
        problem.setRequiredScopes(Collections.singletonList("required-scope"));
        assertThat(problem.getRequiredScopes()).isUnmodifiable().containsExactly("required-scope");
    }

    @Test
    void requiredScopesVarargs() {
        MissingScopeProblem problem = new MissingScopeProblem();
        problem.setRequiredScopes("required-scope");
        assertThat(problem.getRequiredScopes()).isUnmodifiable().containsExactly("required-scope");
    }

    @Test
    void addRequiredScope() {
        MissingScopeProblem problem = new MissingScopeProblem();
        problem.addRequiredScope("required-scope");
        assertThat(problem.getRequiredScopes()).isUnmodifiable().containsExactly("required-scope");
    }

    @Test
    void problemTypeAnnotation() {
        assertThat(MissingScopeProblem.class).hasAnnotation(ProblemType.class);
        assertThat(MissingScopeProblem.class.getAnnotation(ProblemType.class).value())
                .isEqualTo("urn:problem-type:belgif:missingScope");
    }

    @Test
    void equalsAndHashCode() {
        MissingScopeProblem problem = new MissingScopeProblem("required-scope");
        MissingScopeProblem equal = new MissingScopeProblem("required-scope");
        MissingScopeProblem other = new MissingScopeProblem("other-scope");
        MissingScopeProblem otherBis = new MissingScopeProblem();
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
