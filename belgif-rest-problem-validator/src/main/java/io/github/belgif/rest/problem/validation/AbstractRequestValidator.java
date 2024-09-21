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
 *
 * @param <V> self-referencing AbstractRequestValidator type (for extensible builder pattern)
 */
public abstract class AbstractRequestValidator<V extends AbstractRequestValidator<V>> {

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
    public V ssin(Input<String> ssin) {
        if (ssin != null && ssin.getValue() != null) {
            addValidator(new SsinValidator(ssin));
        }
        return getThis();
    }

    /**
     * Validate the structure of a list of Belgif openapi-person-identifier SSIN.
     *
     * @param ssins the SSINs to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-person-identifier">openapi-person-identifier</a>
     */
    public V ssins(Input<List<String>> ssins) {
        if (ssins != null && ssins.getValue() != null) {
            int index = 0;
            for (String ssin : ssins.getValue()) {
                ssin(new Input<>(ssins.getIn(), ssins.getName() + "[" + index + "]", ssin));
                index++;
            }
        }
        return getThis();
    }

    /**
     * Validate the structure of a Belgif openapi-organization-identifier EnterpriseNumber.
     *
     * @param enterpriseNumber the enterprise number to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
     */
    public V enterpriseNumber(Input<String> enterpriseNumber) {
        if (enterpriseNumber != null && enterpriseNumber.getValue() != null) {
            addValidator(new EnterpriseNumberValidator(enterpriseNumber));
        }
        return getThis();
    }

    /**
     * Validate the structure of a Belgif openapi-organization-identifier EstablishmentUnitNumber.
     *
     * @param establishmentUnitNumber the establishment unit number to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
     */
    public V establishmentUnitNumber(Input<String> establishmentUnitNumber) {
        if (establishmentUnitNumber != null && establishmentUnitNumber.getValue() != null) {
            addValidator(new EstablishmentUnitNumberValidator(establishmentUnitNumber));
        }
        return getThis();
    }

    /**
     * Validate the structure of a Belgif openapi-time Period or PeriodOptionalEnd.
     *
     * @param period the Period or PeriodOptionalEnd to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public V period(Input<?> period) {
        if (period != null && period.getValue() != null) {
            addValidator(new PeriodValidator(period));
        }
        return getThis();
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
    public <T extends Temporal & Comparable<? super T>> V period(Input<T> start, Input<T> end) {
        addValidator(new PeriodMultiInputValidator<>(start, end));
        return getThis();
    }

    /**
     * Validate the structure of a Belgif openapi-time IncompleteDate.
     *
     * @param incompleteDate the IncompleteDate to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public V incompleteDate(Input<String> incompleteDate) {
        if (incompleteDate != null && incompleteDate.getValue() != null) {
            addValidator(new IncompleteDateValidator(incompleteDate));
        }
        return getThis();
    }

    /**
     * Validate the structure of a Belgif openapi-time YearMonth.
     *
     * @param yearMonth the YearMonth to validate
     * @return this RequestValidator
     * @see <a href="https://github.com/belgif/openapi-time">openapi-time</a>
     */
    public V yearMonth(Input<String> yearMonth) {
        if (yearMonth != null && yearMonth.getValue() != null) {
            addValidator(new YearMonthValidator(yearMonth));
        }
        return getThis();
    }

    /**
     * Validate that exactly one of the given inputs is present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public V exactlyOneOf(Input<?>... inputs) {
        addValidator(new ExactlyOneOfValidator(Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Validate that any of the given inputs is present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public V anyOf(Input<?>... inputs) {
        addValidator(new AnyOfValidator(Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Validate that all or none of the given inputs are present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public V zeroOrAllOf(Input<?>... inputs) {
        addValidator(new ZeroOrAllOfValidator(Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Validate that exactly one or none of the given inputs are present.
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public V zeroOrExactlyOneOf(Input<?>... inputs) {
        addValidator(new ZeroOrExactlyOneOfValidator(Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Validate that the given inputs are equal (according to {@link Object#equals(Object)}).
     *
     * @param inputs the inputs to validate
     * @return this RequestValidator
     */
    public V equal(Input<?>... inputs) {
        addValidator(new EqualValidator(Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Validate that the given input is either null, or equal to another given input if present.
     *
     * @param nullableInput the input that can be null or equal
     * @param mustMatch the reference input that must be matched against
     * @return this RequestValidator
     */
    public V nullOrEqual(Input<?> nullableInput, Input<?> mustMatch) {
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
    public <T> V refData(Input<T> input, Collection<T> allowedRefData) {
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
    public <T> V refData(Input<T> input, Supplier<Collection<T>> allowedRefDataSupplier) {
        if (input != null && input.getValue() != null) {
            addValidator(new RefDataCollectionValidator<T>(input, allowedRefDataSupplier.get()));
        }
        return getThis();
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataPredicate the predicate for verifying the reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V refData(Input<T> input, Predicate<T> allowedRefDataPredicate) {
        if (input != null && input.getValue() != null) {
            addValidator(new RefDataPredicateValidator<T>(input, allowedRefDataPredicate));
        }
        return getThis();
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefData the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V refDatas(Input<List<T>> input, Collection<T> allowedRefData) {
        return (V) refDatas(input, () -> allowedRefData);
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataSupplier the supplier of the allowed reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V refDatas(Input<List<T>> input, Supplier<Collection<T>> allowedRefDataSupplier) {
        if (input != null && input.getValue() != null && !input.getValue().isEmpty()) {
            Collection<T> allowedRefData = allowedRefDataSupplier.get();
            int index = 0;
            for (T value : input.getValue()) {
                refData(new Input<T>(input.getIn(), input.getName() + "[" + index + "]", value), allowedRefData);
                index++;
            }
        }
        return getThis();
    }

    /**
     * Validate that the given input is in the allowed reference data.
     *
     * @param input the input to validate
     * @param allowedRefDataPredicate the predicate for verifying the reference data
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V refDatas(Input<List<T>> input, Predicate<T> allowedRefDataPredicate) {
        if (input != null && input.getValue() != null && !input.getValue().isEmpty()) {
            int index = 0;
            for (T value : input.getValue()) {
                refData(new Input<T>(input.getIn(), input.getName() + "[" + index + "]", value),
                        allowedRefDataPredicate);
                index++;
            }
        }
        return getThis();
    }

    /**
     * Validate that the given input is not present.
     *
     * @param input the input to validate
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V reject(Input<T> input) {
        if (input != null) {
            addValidator(new RejectedInputValidator<>(input));
        }
        return getThis();
    }

    /**
     * Validate that the given input is present.
     *
     * @param input the input to validate
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T> V require(Input<T> input) {
        if (input != null) {
            addValidator(new RequiredInputValidator<>(input));
        }
        return getThis();
    }

    /**
     * Validate that all the given inputs are present when the given target input is present.
     *
     * @param target the target input
     * @param inputs the inputs that are required when the target input is present
     * @return this RequestValidator
     */
    public V requireIfPresent(Input<?> target, Input<?>... inputs) {
        addValidator(new RequiredIfPresentValidator(target, Arrays.asList(inputs)));
        return getThis();
    }

    /**
     * Conditionally register input validators.
     *
     * @param condition the condition
     * @param requestValidatorConsumer the RequestValidator consumer that will only be called if condition is true
     * @return this RequestValidator
     */
    public V when(boolean condition, Consumer<V> requestValidatorConsumer) {
        if (condition) {
            requestValidatorConsumer.accept(getThis());
        }
        return getThis();
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
    public <T extends Comparable<T>> V range(Input<T> input, T min, T max) {
        if (input != null && input.getValue() != null) {
            addValidator(new RangeValidator<>(input, min, max));
        }
        return getThis();
    }

    /**
     * Validate that the given input value is not less than the given minimum.
     *
     * @param input the input to validate
     * @param min the minimum (inclusive)
     * @param <T> the input type
     * @return this RequestValidator
     */
    public <T extends Comparable<T>> V minimum(Input<T> input, T min) {
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
    public <T extends Comparable<T>> V maximum(Input<T> input, T max) {
        return range(input, null, max);
    }

    /**
     * Register a custom {@link InputValidator}.
     *
     * @param validator the custom validator
     * @return this RequestValidator
     */
    public V custom(InputValidator validator) {
        if (validator != null) {
            addValidator(validator);
        }
        return getThis();
    }

    protected abstract V getThis();

    protected void addValidator(InputValidator validator) {
        this.validators.add(validator);
    }

}
