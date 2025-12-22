package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EmployerIdValidatorTest {

    @ParameterizedTest
    @ValueSource(longs = { 100006L, 212345609L, 312345625L, 200000031L, 499999982L, 187995796L, 168676597L })
    void okNssoNumber(Long employerId) {
        assertThat(new EmployerIdValidator(Input.body("test", employerId)).validate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(longs = { 5134794036L, 5000000120L, 5999999989L, 5678901277L })
    void okProvisionalNssoNumber(Long employerId) {
        assertThat(new EmployerIdValidator(Input.body("test", employerId)).validate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(longs = { 43220065L, 2130057L, 22300094L, 5170096L, 55290097L })
    void okPplNumber(Long employerId) {
        assertThat(new EmployerIdValidator(Input.body("test", employerId)).validate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(longs = { 196L, 4000000100L, 6999999999L, 5678901279L, 1000000047L, 5000000121L })
    void nok(Long employerId) {
        assertThat(new EmployerIdValidator(Input.body("test", employerId)).validate())
                .contains(InputValidationIssues.invalidEmployerId(BODY, "test", employerId));
    }

}
