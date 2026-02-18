package io.github.belgif.rest.problem.quarkus.server.i18n;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadGatewayProblem;

class ProblemI18NTest {

    @Test
    void createProblem() {
        assertDoesNotThrow(BadGatewayProblem::new);
    }

}
