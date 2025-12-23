package io.github.belgif.rest.problem.quarkus.it;

import static io.github.belgif.rest.problem.api.InputValidationIssues.schemaViolation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import jakarta.ws.rs.ext.ParamConverter;

import io.github.belgif.rest.problem.BadRequestProblem;

public class LocalDateConverter implements ParamConverter<LocalDate> {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public LocalDate fromString(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }

        try {
            return LocalDate.parse(value, LOCAL_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestProblem(schemaViolation(null, null, value, "date has invalid format"));
        }
    }

    @Override
    public String toString(final LocalDate value) {
        return LOCAL_DATE_FORMATTER.format(value);
    }

}
