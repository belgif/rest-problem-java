package io.github.belgif.rest.problem.validation;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-person-identifier SSIN.
 *
 * @see <a href="https://github.com/belgif/openapi-person-identifier">openapi-person-identifier</a>
 */
class SsinValidator extends AbstractSingleInputValidator<String> {

    private static final long YEAR_2000_PREFIX = 200_000_000_000L;

    /** value for unspecified fields. */
    private static final int UNSPECIFIED = 0;

    SsinValidator(Input<String> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        if (!isValidSsin(getValue())) {
            return Optional.of(InputValidationIssues.invalidSsin(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }

    private boolean isValidSsin(String value) {
        // Belgif openapi-person-identifier Ssin already enforces ^\d{11}$ regex
        long ssin = Long.parseLong(value);
        // extract parts
        int counterPart = (int) ((ssin / 100) % 1000);
        int dayPart = (int) ((ssin / 100_000) % 100);
        int monthPart = (int) ((ssin / 10_000_000) % 100);
        int month = monthPart % 20;
        int yearPart = (int) (ssin / 1000_000_000);
        // validate month
        if (monthPart > 52 || month > 12) {
            return false;
        }
        // validate day
        if (dayPart != UNSPECIFIED && month > UNSPECIFIED && (dayPart > Month.of(month).maxLength())) {
            return false;
        }
        // validate counter
        if (monthPart <= 12 && (counterPart == 0 || counterPart == 999)) {
            return false;
        }
        // validate checksum
        boolean year2000 = false;
        if (!Checksum.validateModulo97Checksum(ssin)) {
            if (Checksum.validateModulo97Checksum(YEAR_2000_PREFIX + ssin)) {
                year2000 = true;
            } else {
                return false;
            }
        }
        // validate date structure
        if (dayPart != UNSPECIFIED) {
            if (month == UNSPECIFIED) {
                return false;
            }
            try {
                LocalDate.of(year2000 ? 2000 + yearPart : 1900 + yearPart, month, dayPart);
            } catch (DateTimeException e) {
                return false;
            }
        }

        return validateBirthDate(year2000, yearPart, month, dayPart);
    }

    private boolean validateBirthDate(boolean year2000, int yearPart, int month, int dayPart) {
        // validate if birth date is in the future
        int birthDateIntRepresentation =
                ((year2000 ? yearPart + 2000 : yearPart + 1900) * 10000) + (month * 100) + dayPart;

        LocalDate now = LocalDate.now();
        int nowIntRepresentation = (now.getYear() * 10000) + (now.getMonthValue() * 100) + now.getDayOfMonth();

        return birthDateIntRepresentation <= nowIntRepresentation;
    }

}
