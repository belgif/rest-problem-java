package io.github.belgif.rest.problem.validation;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;

/**
 * Performs validation on input parameters of an API request.
 *
 * <p>
 * This validation does not stop on the first invalid input. It performs all configured validations and if any of
 * them failed, a {@link BadRequestProblem} is thrown, containing each encountered {@link InputValidationIssue}.
 * </p>
 *
 * @see BadRequestProblem
 * @see InputValidationIssue
 * @see InputValidator
 */
public class RequestValidator {

    private final List<InputValidator> validators = new ArrayList<>();

    /**
     * Perform all configured validations.
     *
     * @throws BadRequestProblem if the validation fails, containing each encountered {@link InputValidationIssue}
     */
    public void validate() throws BadRequestProblem {
        List<InputValidationIssue> issues = validators.stream()
                .map(InputValidator::validate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (!issues.isEmpty()) {
            throw new BadRequestProblem(issues);
        }
    }

    /**
     * Validate the structure of a Belgif openapi-person-identifier SSIN.
     *
     * @param ssin the SSIN to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-person-identifier">openapi-person-identifier</a>
     */
    public RequestValidator ssin(Input<String> ssin) {
        if (ssin != null && ssin.getValue() != null) {
            validators.add(new SsinValidator(ssin));
        }
        return this;
    }

    /**
     * Validate the structure of a list of Belgif openapi-person-identifier SSIN.
     *
     * @param ssins the SSINs to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-person-identifier">openapi-person-identifier</a>
     */
    public RequestValidator ssins(Input<List<String>> ssins) {
        if (ssins != null && ssins.getValue() != null) {
            int index = 0;
            for (String ssin : ssins.getValue()) {
                ssin(new Input<>(ssins.getIn(), ssins.getName() + "[" + index + "]", ssin));
                index++;
            }
        }
        return this;
    }

    /**
     * Validate the structure of a Belgif openapi-organization-identifier EnterpriseNumber.
     *
     * @param enterpriseNumber the enterprise number to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
     */
    public RequestValidator enterpriseNumber(Input<String> enterpriseNumber) {
        if (enterpriseNumber != null && enterpriseNumber.getValue() != null) {
            validators.add(new EnterpriseNumberValidator(enterpriseNumber));
        }
        return this;
    }

    /**
     * Validate the structure of a Belgif openapi-organization-identifier EstablishmentUnitNumber.
     *
     * @param establishmentUnitNumber the establishment unit number to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
     */
    public RequestValidator establishmentUnitNumber(Input<String> establishmentUnitNumber) {
        if (establishmentUnitNumber != null && establishmentUnitNumber.getValue() != null) {
            validators.add(new EstablishmentUnitNumberValidator(establishmentUnitNumber));
        }
        return this;
    }

    /**
     * Validate the structure of a Belgif openapi-time Period or PeriodOptionalEnd.
     *
     * @param period the Period or PeriodOptionalEnd to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public RequestValidator period(Input<?> period) {
        if (period != null && period.getValue() != null) {
            validators.add(new PeriodValidator(period));
        }
        return this;
    }

    /**
     * Validate that two comparable temporal inputs make a valid period.
     *
     * <p>
     * This can be used with types like {@link LocalDate}, {@link java.time.LocalDateTime},
     * {@link java.time.OffsetDateTime}, {@link java.time.Year}, {@link java.time.YearMonth}, ...
     * </p>
     *
     * @param start the input with the start
     * @param end the input with the end
     * @param <T> the comparable temporal type
     * @return this RequestValidator
     */
    public <T extends Temporal & Comparable<? super T>> RequestValidator period(Input<T> start, Input<T> end) {
        validators.add(new PeriodMultiInputValidator<>(start, end));
        return this;
    }

    /**
     * Validate the structure of a Belgif openapi-time IncompleteDate.
     *
     * @param incompleteDate the IncompleteDate to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public RequestValidator incompleteDate(Input<String> incompleteDate) {
        if (incompleteDate != null && incompleteDate.getValue() != null) {
            validators.add(new IncompleteDateValidator(incompleteDate));
        }
        return this;
    }

    /**
     * Validate the structure of a Belgif openapi-time YearMonth.
     *
     * @param yearMonth the YearMonth to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public RequestValidator yearMonth(Input<String> yearMonth) {
        if (yearMonth != null && yearMonth.getValue() != null) {
            validators.add(new YearMonthValidator(yearMonth));
        }
        return this;
    }

    /**
     * Validate that exactly one of the given inputs is present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public RequestValidator exactlyOneOf(Input<?>... inputs) {
        validators.add(new ExactlyOneOfValidator(Arrays.asList(inputs)));
        return this;
    }

    /**
     * Validate that any of the given inputs is present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public RequestValidator anyOf(Input<?>... inputs) {
        validators.add(new AnyOfValidator(Arrays.asList(inputs)));
        return this;
    }

    /**
     * Validate that all or none of the given inputs are present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public RequestValidator zeroOrAllOf(Input<?>... inputs) {
        validators.add(new ZeroOrAllOfValidator(Arrays.asList(inputs)));
        return this;
    }

    /**
     * Validate that exactly one or none of the given inputs are present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public RequestValidator zeroOrExactlyOneOf(Input<?>... inputs) {
        validators.add(new ZeroOrExactlyOneOfValidator(Arrays.asList(inputs)));
        return this;
    }

    /**
     * Validate that the given inputs are equal (according to {@link Object#equals(Object)}).
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public RequestValidator equal(Input<?>... inputs) {
        validators.add(new EqualValidator(Arrays.asList(inputs)));
        return this;
    }

    /**
     * Validate that the given input is either null, or equal to another given input if present.
     *
     * @param nullableInput the input that can be null or equal
     * @param mustMatch the reference input that must be matched against
     * @return this RequestValidator
     */
    public RequestValidator nullOrEqual(Input<?> nullableInput, Input<?> mustMatch) {
        return when(nullableInput != null && nullableInput.getValue() != null,
                validator -> validator.equal(nullableInput, mustMatch));
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefData the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refData(Input<T> input, Collection<T> allowedRefData) {
        return refData(input, () -> allowedRefData);
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataSupplier the supplier of the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refData(Input<T> input, Supplier<Collection<T>> allowedRefDataSupplier) {
        if (input != null && input.getValue() != null) {
            validators.add(new RefDataCollectionValidator<T>(input, allowedRefDataSupplier.get()));
        }
        return this;
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataPredicate the predicate for verifying the reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refData(Input<T> input, Predicate<T> allowedRefDataPredicate) {
        if (input != null && input.getValue() != null) {
            validators.add(new RefDataPredicateValidator<T>(input, allowedRefDataPredicate));
        }
        return this;
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefData the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refDatas(Input<List<T>> input, Collection<T> allowedRefData) {
        return refDatas(input, () -> allowedRefData);
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataSupplier the supplier of the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refDatas(Input<List<T>> input, Supplier<Collection<T>> allowedRefDataSupplier) {
        if (input != null && input.getValue() != null && !input.getValue().isEmpty()) {
            Collection<T> allowedRefData = allowedRefDataSupplier.get();
            int index = 0;
            for (T value : input.getValue()) {
                refData(new Input<T>(input.getIn(), input.getName() + "[" + index + "]", value), allowedRefData);
                index++;
            }
        }
        return this;
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataPredicate the predicate for verifying the reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator refDatas(Input<List<T>> input, Predicate<T> allowedRefDataPredicate) {
        if (input != null && input.getValue() != null && !input.getValue().isEmpty()) {
            int index = 0;
            for (T value : input.getValue()) {
                refData(new Input<T>(input.getIn(), input.getName() + "[" + index + "]", value),
                        allowedRefDataPredicate);
                index++;
            }
        }
        return this;
    }

    /**
     * Validate that the given input is not present.
     *
     * @param input the input to validate
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator reject(Input<T> input) {
        if (input != null) {
            validators.add(new RejectedInputValidator<>(input));
        }
        return this;
    }

    /**
     * Validate that the given input is present.
     *
     * @param input the input to validate
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> RequestValidator require(Input<T> input) {
        if (input != null) {
            validators.add(new RequiredInputValidator<>(input));
        }
        return this;
    }

    /**
     * Conditionally register input validators.
     *
     * @param condition the condition
     * @param requestValidatorConsumer the RequestValidator consumer that will only be called if condition is true
     * @return this RequestValidator
     */
    public RequestValidator when(boolean condition, Consumer<RequestValidator> requestValidatorConsumer) {
        if (condition) {
            requestValidatorConsumer.accept(this);
        }
        return this;
    }

    /**
     * Validate that the given input value is in the given [min, max] range.
     *
     * @param input the input to validate
     * @param min the minimum (inclusive)
     * @param max the maximum (inclusive)
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T extends Comparable<T>> RequestValidator range(Input<T> input, T min, T max) {
        if (input != null && input.getValue() != null) {
            validators.add(new RangeValidator<>(input, min, max));
        }
        return this;
    }

    /**
     * Validate that the given input value is not less than the given minimum.
     *
     * @param input the input to validate
     * @param min the minimum (inclusive)
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T extends Comparable<T>> RequestValidator minimum(Input<T> input, T min) {
        return range(input, min, null);
    }

    /**
     * Validate that the given input value does not exceed the given maximum.
     *
     * @param input the input to validate
     * @param max the maximum (inclusive)
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T extends Comparable<T>> RequestValidator maximum(Input<T> input, T max) {
        return range(input, null, max);
    }

    /**
     * Register a custom {@link InputValidator}.
     *
     * @param validator the custom validator
     * @return this RequestValidator
     */
    public RequestValidator custom(InputValidator validator) {
        if (validator != null) {
            validators.add(validator);
        }
        return this;
    }
}
