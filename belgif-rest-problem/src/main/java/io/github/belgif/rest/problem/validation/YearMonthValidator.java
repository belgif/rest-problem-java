package io.github.belgif.rest.problem.validation;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-time YearMonth.
 *
 * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
 */
class YearMonthValidator extends AbstractSingleInputValidator<String> {

    YearMonthValidator(Input<String> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        try {
            YearMonth.parse(getValue());
        } catch (DateTimeParseException e) {
            return Optional.of(InputValidationIssues.invalidYearMonth(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
