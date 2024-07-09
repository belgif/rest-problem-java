package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for creating InputValidationIssues for common request validations.
 *
 * @see InputValidationIssue
 */
public class InputValidationIssues {

    // Belgif input-validation issue types

    public static final URI ISSUE_TYPE_SCHEMA_VIOLATION =
            URI.create("urn:problem-type:belgif:input-validation:schemaViolation");
    public static final URI ISSUE_TYPE_UNKNOWN_INPUT =
            URI.create("urn:problem-type:belgif:input-validation:unknownInput");

    // CBSS input-validation issue types for Belgif openapi types
    // TODO: standardize @ Belgif: https://github.com/belgif/rest-guide/issues/126

    public static final URI ISSUE_TYPE_INVALID_STRUCTURE =
            URI.create("urn:problem-type:cbss:input-validation:invalidStructure");
    public static final URI ISSUE_TYPE_OUT_OF_RANGE =
            URI.create("urn:problem-type:cbss:input-validation:outOfRange");
    public static final URI ISSUE_TYPE_REFERENCED_RESOURCE_NOT_FOUND =
            URI.create("urn:problem-type:cbss:input-validation:referencedResourceNotFound");
    public static final URI ISSUE_TYPE_REJECTED_INPUT =
            URI.create("urn:problem-type:cbss:input-validation:rejectedInput");
    public static final URI ISSUE_TYPE_REQUIRED_INPUT =
            URI.create("urn:problem-type:cbss:input-validation:requiredInput");

    public static final URI ISSUE_TYPE_REPLACED_SSIN =
            URI.create("urn:problem-type:cbss:input-validation:replacedSsin");
    public static final URI ISSUE_TYPE_CANCELED_SSIN =
            URI.create("urn:problem-type:cbss:input-validation:canceledSsin");
    public static final URI ISSUE_TYPE_INVALID_PERIOD =
            URI.create("urn:problem-type:cbss:input-validation:invalidPeriod");

    // CBSS input-validation issue types for cross-parameter validation
    // TODO: standardize @ Belgif: https://github.com/belgif/rest-guide/issues/113

    public static final URI ISSUE_TYPE_EXACTLY_ONE_OF_EXPECTED =
            URI.create("urn:problem-type:cbss:input-validation:exactlyOneOfExpected");
    public static final URI ISSUE_TYPE_ANY_OF_EXPECTED =
            URI.create("urn:problem-type:cbss:input-validation:anyOfExpected");
    public static final URI ISSUE_TYPE_ZERO_OR_EXACTLY_ONE_OF_EXPECTED =
            URI.create("urn:problem-type:cbss:input-validation:zeroOrExactlyOneOfExpected");
    public static final URI ISSUE_TYPE_ZERO_OR_ALL_OF_EXPECTED =
            URI.create("urn:problem-type:cbss:input-validation:zeroOrAllOfExpected");
    public static final URI ISSUE_TYPE_EQUAL_EXPECTED =
            URI.create("urn:problem-type:cbss:input-validation:equalExpected");

    private InputValidationIssues() {
    }

    public static InputValidationIssue schemaViolation(InEnum in, String name, Object value, String detail) {
        return new InputValidationIssue(ISSUE_TYPE_SCHEMA_VIOLATION,
                "Input value is invalid with respect to the schema")
                .detail(detail)
                .in(in, name, value);
    }

    public static InputValidationIssue unknownInput(InEnum in, String name, Object value) {
        return new InputValidationIssue(ISSUE_TYPE_UNKNOWN_INPUT, "Unknown input")
                .detail(String.format("Input %s is unknown", name))
                .in(in, name, value);
    }

    public static InputValidationIssue invalidStructure(InEnum in, String name, Object value, String detail) {
        return new InputValidationIssue(ISSUE_TYPE_INVALID_STRUCTURE,
                "Input value has invalid structure")
                .detail(detail)
                .in(in, name, value);
    }

    public static <T extends Comparable<T>> InputValidationIssue outOfRange(InEnum in, String name, T value, T min,
            T max) {
        if (min == null && max == null) {
            throw new IllegalArgumentException("At least one of min, max must be non-null");
        }
        InputValidationIssue issue =
                new InputValidationIssue(ISSUE_TYPE_OUT_OF_RANGE, "Input value is out of range")
                        .in(in, name, value);
        if (min != null && max != null) {
            issue.detail(String.format("Input value %s = %s is out of range [%s, %s]", name, value, min, max))
                    .additionalProperty("minimum", min.toString())
                    .additionalProperty("maximum", max.toString());
        } else if (min != null) {
            issue.detail(String.format("Input value %s = %s should be at least %s", name, value, min))
                    .additionalProperty("minimum", min.toString());
        } else {
            issue.detail(String.format("Input value %s = %s should not exceed %s", name, value, max))
                    .additionalProperty("maximum", max.toString());
        }
        return issue;
    }

    public static InputValidationIssue referencedResourceNotFound(InEnum in, String name, Object value) {
        return new InputValidationIssue(ISSUE_TYPE_REFERENCED_RESOURCE_NOT_FOUND, "Referenced resource not found")
                .detail(String.format("Referenced resource %s = '%s' does not exist", name, value))
                .in(in, name, value);
    }

    public static InputValidationIssue rejectedInput(InEnum in, String name, Object value) {
        return new InputValidationIssue(ISSUE_TYPE_REJECTED_INPUT, "Input is not allowed in this context")
                .detail(String.format("Input %s is not allowed in this context", name))
                .in(in, name, value);
    }

    public static InputValidationIssue requiredInput(InEnum in, String name) {
        return new InputValidationIssue(ISSUE_TYPE_REQUIRED_INPUT, "Input is required in this context")
                .detail(String.format("Input %s is required in this context", name))
                .in(in, name, null);
    }

    public static InputValidationIssue requiredInputsIfPresent(Input<?> target, List<Input<?>> inputs) {
        InputValidationIssue issue =
                new InputValidationIssue(ISSUE_TYPE_REQUIRED_INPUT, "Input is required in this context")
                        .detail(String.format("All of these inputs must be present if %s is present: %s",
                                target.getName(), getJoinedNames(inputs)));
        issue.addInput(target);
        issue.addInputs(inputs);
        return issue;
    }

    public static InputValidationIssue replacedSsin(InEnum in, String name, String ssin, String newSsin) {
        return new InputValidationIssue(ISSUE_TYPE_REPLACED_SSIN, "SSIN has been replaced, use new SSIN")
                .detail(String.format("SSIN %s has been replaced by %s", ssin, newSsin))
                .in(in, name, ssin)
                .additionalProperty("replacedBy", newSsin);
    }

    public static InputValidationIssue replacedSsin(InEnum in, String name, String ssin, String newSsin, URI newHref) {
        return replacedSsin(in, name, ssin, newSsin).additionalProperty("replacedByHref", String.valueOf(newHref));
    }

    public static InputValidationIssue canceledSsin(InEnum in, String name, String ssin) {
        return new InputValidationIssue(ISSUE_TYPE_CANCELED_SSIN, "SSIN has been canceled")
                .detail(String.format("SSIN %s has been canceled", ssin))
                .in(in, name, ssin);
    }

    public static InputValidationIssue invalidSsin(InEnum in, String name, String ssin) {
        return invalidStructure(in, name, ssin, String.format("SSIN %s is invalid", ssin));
    }

    public static InputValidationIssue unknownSsin(InEnum in, String name, String ssin) {
        return referencedResourceNotFound(in, name, ssin).detail(String.format("SSIN %s does not exist", ssin));
    }

    public static InputValidationIssue invalidPeriod(InEnum in, String name, Object period) {
        return new InputValidationIssue(ISSUE_TYPE_INVALID_PERIOD, "Period is invalid")
                .detail("endDate should not precede startDate")
                .in(in, name, period);
    }

    public static <T extends Temporal & Comparable<? super T>> InputValidationIssue invalidPeriod(
            Input<T> start, Input<T> end) {
        return new InputValidationIssue(ISSUE_TYPE_INVALID_PERIOD, "Period is invalid")
                .detail(String.format("%s should not precede %s", end.getName(), start.getName()))
                .inputs(Arrays.asList(start, end));
    }

    public static InputValidationIssue invalidIncompleteDate(InEnum in, String name, String incompleteDate) {
        return invalidStructure(in, name, incompleteDate,
                String.format("Incomplete date %s is invalid", incompleteDate));
    }

    public static InputValidationIssue invalidYearMonth(InEnum in, String name, String yearMonth) {
        return invalidStructure(in, name, yearMonth, String.format("Year month %s is invalid", yearMonth));
    }

    public static InputValidationIssue invalidEnterpriseNumber(InEnum in, String name, String enterpriseNumber) {
        return invalidStructure(in, name, enterpriseNumber,
                String.format("Enterprise number %s is invalid", enterpriseNumber));
    }

    public static InputValidationIssue invalidEstablishmentUnitNumber(InEnum in, String name,
            String establishmentUnitNumber) {
        return invalidStructure(in, name, establishmentUnitNumber,
                String.format("Establishment unit number %s is invalid", establishmentUnitNumber));
    }

    public static InputValidationIssue exactlyOneOfExpected(List<Input<?>> inputs) {
        return new InputValidationIssue(ISSUE_TYPE_EXACTLY_ONE_OF_EXPECTED,
                "Exactly one of these inputs must be present")
                .detail(String.format("Exactly one of these inputs must be present: %s",
                        getJoinedNames(inputs)))
                .inputs(inputs);
    }

    public static InputValidationIssue anyOfExpected(List<Input<?>> inputs) {
        return new InputValidationIssue(ISSUE_TYPE_ANY_OF_EXPECTED, "Any of these inputs must be present")
                .detail(String.format("Any of these inputs must be present: %s", getJoinedNames(inputs)))
                .inputs(inputs);
    }

    public static InputValidationIssue zeroOrExactlyOneOfExpected(List<Input<?>> inputs) {
        return new InputValidationIssue(ISSUE_TYPE_ZERO_OR_EXACTLY_ONE_OF_EXPECTED,
                "Exactly one or none of these inputs must be present")
                .detail(String.format("Exactly one or none of these inputs must be present: %s",
                        getJoinedNames(inputs)))
                .inputs(inputs);
    }

    public static InputValidationIssue zeroOrAllOfExpected(List<Input<?>> inputs) {
        return new InputValidationIssue(ISSUE_TYPE_ZERO_OR_ALL_OF_EXPECTED,
                "All or none of these inputs must be present")
                .detail(String.format("All or none of these inputs must be present: %s",
                        getJoinedNames(inputs)))
                .inputs(inputs);
    }

    public static InputValidationIssue equalExpected(List<Input<?>> inputs) {
        return new InputValidationIssue(ISSUE_TYPE_EQUAL_EXPECTED, "These inputs must be equal")
                .detail(String.format("These inputs must be equal: %s", getJoinedNames(inputs)))
                .inputs(inputs);
    }

    private static String getJoinedNames(List<Input<?>> inputs) {
        return inputs == null || inputs.isEmpty() ? ""
                : inputs.stream().map(Input::getName).collect(Collectors.joining(", "));
    }
}
