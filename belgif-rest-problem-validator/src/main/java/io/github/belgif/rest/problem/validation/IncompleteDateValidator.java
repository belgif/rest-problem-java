package io.github.belgif.rest.problem.validation;

import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.regex.Pattern;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-time IncompleteDate.
 *
 * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
 */
class IncompleteDateValidator extends AbstractSingleInputValidator<String> {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{4}-[0-1][0-9]-[0-3][0-9]$");

    /** maximum valid value of month part. */
    private static final int MONTH_MAX = 12;

    /** value for unspecified fields. */
    private static final int UNSPECIFIED = 0;

    IncompleteDateValidator(Input<String> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        if (!PATTERN.matcher(getValue()).matches()) {
            return Optional.of(InputValidationIssues.invalidIncompleteDate(getIn(), getName(), getValue()));
        }
        try {
            TemporalAccessor temporal =
                    DateTimeFormatter.ISO_LOCAL_DATE.parseUnresolved(getValue(), new ParsePosition(0));

            int yearPart = (int) temporal.getLong(ChronoField.YEAR);
            int monthPart = (int) temporal.getLong(ChronoField.MONTH_OF_YEAR);
            int dayPart = (int) temporal.getLong(ChronoField.DAY_OF_MONTH);

            // Belgif openapi-time already enforces the year max, month min and day min validation
            if (monthPart > MONTH_MAX || (monthPart == UNSPECIFIED && dayPart != UNSPECIFIED)) {
                return Optional.of(InputValidationIssues.invalidIncompleteDate(getIn(), getName(), getValue()));
            }

            if (dayPart != UNSPECIFIED) {
                // verify if it is possible to create a local date with the date parts. If not, a DateTimException is
                // thrown
                LocalDate.of(yearPart, monthPart, dayPart);
            }
        } catch (DateTimeException e) {
            return Optional.of(InputValidationIssues.invalidIncompleteDate(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
