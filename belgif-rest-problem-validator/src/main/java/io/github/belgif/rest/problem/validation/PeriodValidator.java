package io.github.belgif.rest.problem.validation;

import java.time.LocalDate;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validate the structure of a Belgif openapi-time Period or PeriodOptionalEnd.
 *
 * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
 */
class PeriodValidator extends AbstractSingleInputValidator<Object> {

    PeriodValidator(Input<?> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        LocalDate startDate = TimeReflectionUtil.getStartDate(getValue());
        LocalDate endDate = TimeReflectionUtil.getEndDate(getValue());

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            return Optional.of(InputValidationIssues.invalidPeriod(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
