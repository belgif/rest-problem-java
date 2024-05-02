package io.github.belgif.rest.problem.codegen;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.belgif.rest.problem.codegen.openapi.api.LegacyApi;
import io.github.belgif.rest.problem.codegen.openapi.api.ProblemApi;
import io.github.belgif.rest.problem.codegen.openapi.model.SuccessResponse;

class OpenApiGeneratorIT {

    @ParameterizedTest
    @ValueSource(strings = {
            "Problem", "InputValidationProblem", "InputValidationIssue",
            "InvalidParamProblem", "InvalidParam" })
    void noProblemModelClassesAreGenerated(String model) {
        assertThatExceptionOfType(ClassNotFoundException.class).isThrownBy(
                () -> Class.forName("io.github.belgif.rest.problem.codegen.openapi.model." + model));
    }

    @Test
    void apiAndModelClassesAreGenerated() {
        assertThat(ProblemApi.class).hasMethods("getProblem");
        assertThat(LegacyApi.class).hasMethods("getLegacyProblem");
        assertThat(SuccessResponse.class).hasMethods("getMessage");
    }

}
