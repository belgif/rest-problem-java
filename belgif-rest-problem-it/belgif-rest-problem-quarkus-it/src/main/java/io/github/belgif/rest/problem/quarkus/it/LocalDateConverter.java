package io.github.belgif.rest.problem.quarkus.it;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import jakarta.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractInputParamConverterProvider;

@Provider
public class LocalDateConverter extends AbstractInputParamConverterProvider<LocalDate> {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    protected LocalDate fromString(InEnum in, String name, String value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }

        try {
            return LocalDate.parse(value, LOCAL_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestProblem(schemaViolation(in, name, value, "date has invalid format"));
        }
    }

    @Override
    protected String toString(LocalDate value) {
        return LOCAL_DATE_FORMATTER.format(value);
    }

}
