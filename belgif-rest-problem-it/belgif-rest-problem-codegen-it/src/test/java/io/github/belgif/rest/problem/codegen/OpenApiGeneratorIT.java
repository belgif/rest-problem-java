package io.github.belgif.rest.problem.codegen;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OpenApiGeneratorIT {

    @ParameterizedTest
    @ValueSource(strings = {
            "Problem", "InputValidationProblem", "InputValidationIssue",
            "InvalidParamProblem", "InvalidParam" })
    void noProblemModelClassesAreGenerated(String model) {
        assertThatExceptionOfType(ClassNotFoundException.class).isThrownBy(
                () -> Class.forName("io.github.belgif.rest.problem.codegen.openapi.model." + model));
    }

}
