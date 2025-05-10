package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.i18n.I18N;

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
    public static final URI ISSUE_TYPE_INVALID_INPUT =
            URI.create("urn:problem-type:belgif:input-validation:invalidInput");
    public static final URI ISSUE_TYPE_REFERENCED_RESOURCE_NOT_FOUND =
            URI.create("urn:problem-type:belgif:input-validation:referencedResourceNotFound");

    // Belgif-ext input-validation issue types
    public static final URI ISSUE_TYPE_INVALID_STRUCTURE =
            URI.create("urn:problem-type:belgif-ext:input-validation:invalidStructure");
    public static final URI ISSUE_TYPE_OUT_OF_RANGE =
            URI.create("urn:problem-type:belgif-ext:input-validation:outOfRange");
    public static final URI ISSUE_TYPE_REJECTED_INPUT =
            URI.create("urn:problem-type:belgif-ext:input-validation:rejectedInput");
    public static final URI ISSUE_TYPE_REQUIRED_INPUT =
            URI.create("urn:problem-type:belgif-ext:input-validation:requiredInput");
    public static final URI ISSUE_TYPE_INVALID_PERIOD =
            URI.create("urn:problem-type:belgif-ext:input-validation:invalidPeriod");

    // CBSS SSIN input-validation issue types
    public static final URI ISSUE_TYPE_REPLACED_SSIN =
            URI.create("urn:problem-type:cbss:input-validation:replacedSsin");
    public static final URI ISSUE_TYPE_CANCELED_SSIN =
            URI.create("urn:problem-type:cbss:input-validation:canceledSsin");

    // Belgif-ext input-validation issue types for cross-parameter validation
    public static final URI ISSUE_TYPE_EXACTLY_ONE_OF_EXPECTED =
            URI.create("urn:problem-type:belgif-ext:input-validation:exactlyOneOfExpected");
    public static final URI ISSUE_TYPE_ANY_OF_EXPECTED =
            URI.create("urn:problem-type:belgif-ext:input-validation:anyOfExpected");
    public static final URI ISSUE_TYPE_ZERO_OR_EXACTLY_ONE_OF_EXPECTED =
            URI.create("urn:problem-type:belgif-ext:input-validation:zeroOrExactlyOneOfExpected");
    public static final URI ISSUE_TYPE_ZERO_OR_ALL_OF_EXPECTED =
            URI.create("urn:problem-type:belgif-ext:input-validation:zeroOrAllOfExpected");
    public static final URI ISSUE_TYPE_EQUAL_EXPECTED =
            URI.create("urn:problem-type:belgif-ext:input-validation:equalExpected");

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
                .localizedDetail("unknownInput", name)
                .in(in, name, value);
    }

	public static InputValidationIssue invalidInput(InEnum in, String name, Object value, String detail) {
		return new InputValidationIssue(ISSUE_TYPE_INVALID_INPUT, "Invalid input")
				.detail(detail)
				.in(in, name, value);
	}

    public static InputValidationIssue invalidStructure(InEnum in, String name, Object value, String detail) {
        return invalidInputExt(ISSUE_TYPE_INVALID_STRUCTURE, "Input value has invalid structure")
                .detail(detail)
                .in(in, name, value);
    }

    public static <T extends Comparable<T>> InputValidationIssue outOfRange(InEnum in, String name, T value, T min,
            T max) {
        if (min == null && max == null) {
            throw new IllegalArgumentException("At least one of min, max must be non-null");
        }
        InputValidationIssue issue = invalidInputExt(ISSUE_TYPE_OUT_OF_RANGE, "Input value is out of range")
                .in(in, name, value);
        if (min != null && max != null) {
            issue.localizedDetail("outOfRange.minmax", name, value, min, max)
                    .additionalProperty("minimum", min.toString())
                    .additionalProperty("maximum", max.toString());
        } else if (min != null) {
            issue.localizedDetail("outOfRange.min", name, value, min)
                    .additionalProperty("minimum", min.toString());
        } else {
            issue.localizedDetail("outOfRange.max", name, value, max)
                    .additionalProperty("maximum", max.toString());
        }
        return issue;
    }

    public static InputValidationIssue referencedResourceNotFound(InEnum in, String name, Object value) {
        return new InputValidationIssue(ISSUE_TYPE_REFERENCED_RESOURCE_NOT_FOUND, "Referenced resource not found")
                .localizedDetail("referencedResourceNotFound", name, value)
                .in(in, name, value);
    }

    public static InputValidationIssue rejectedInput(InEnum in, String name, Object value) {
        return invalidInputExt(ISSUE_TYPE_REJECTED_INPUT, "Input is not allowed in this context")
                .localizedDetail("rejectedInput", name)
                .in(in, name, value);
    }

    public static InputValidationIssue requiredInput(InEnum in, String name) {
        return invalidInputExt(ISSUE_TYPE_REQUIRED_INPUT, "Input is required in this context")
                .localizedDetail("requiredInput", name)
                .in(in, name, null);
    }

    public static InputValidationIssue requiredInputsIfPresent(Input<?> target, List<Input<?>> inputs) {
        InputValidationIssue issue = invalidInputExt(ISSUE_TYPE_REQUIRED_INPUT, "Input is required in this context")
                .localizedDetail("requiredInput.ifPresent", target.getName(), getJoinedNames(inputs));
        issue.addInput(target);

        if (inputs != null && ProblemConfig.isExtInputsArrayEnabled()) {
            inputs.forEach(issue::addInput);
        }

        return issue;
    }

    public static InputValidationIssue replacedSsin(InEnum in, String name, String ssin, String newSsin) {
        return new InputValidationIssue(ISSUE_TYPE_REPLACED_SSIN, "SSIN has been replaced, use new SSIN")
                .localizedDetail("replacedSsin", ssin, newSsin)
                .in(in, name, ssin)
                .additionalProperty("replacedBy", newSsin);
    }

    public static InputValidationIssue replacedSsin(InEnum in, String name, String ssin, String newSsin, URI newHref) {
        return replacedSsin(in, name, ssin, newSsin).additionalProperty("replacedByHref", String.valueOf(newHref));
    }

    public static InputValidationIssue canceledSsin(InEnum in, String name, String ssin) {
        return new InputValidationIssue(ISSUE_TYPE_CANCELED_SSIN, "SSIN has been canceled")
                .localizedDetail("canceledSsin", ssin)
                .in(in, name, ssin);
    }

    public static InputValidationIssue invalidSsin(InEnum in, String name, String ssin) {
        return invalidStructure(in, name, ssin, I18N.getLocalizedString("invalidStructure.ssin.detail", ssin));
    }

    public static InputValidationIssue unknownSsin(InEnum in, String name, String ssin) {
        return referencedResourceNotFound(in, name, ssin)
                .localizedDetail("referencedResourceNotFound.ssin", ssin);
    }

    public static InputValidationIssue invalidPeriod(InEnum in, String name, Object period) {
        return invalidInputExt(ISSUE_TYPE_INVALID_PERIOD, "Period is invalid")
                .localizedDetail("invalidPeriod", "endDate", "startDate")
                .in(in, name, period);
    }

    public static <T extends Temporal & Comparable<? super T>> InputValidationIssue invalidPeriod(
            Input<T> start, Input<T> end) {
        InputValidationIssue issue = invalidInputExt(ISSUE_TYPE_INVALID_PERIOD, "Period is invalid")
                .localizedDetail("invalidPeriod", end.getName(), start.getName());
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(Arrays.asList(start, end));
        }
        return issue;
    }

    public static InputValidationIssue invalidIncompleteDate(InEnum in, String name, String incompleteDate) {
        return invalidStructure(in, name, incompleteDate,
                I18N.getLocalizedString("invalidStructure.incompleteDate.detail", incompleteDate));
    }

    public static InputValidationIssue invalidYearMonth(InEnum in, String name, String yearMonth) {
        return invalidStructure(in, name, yearMonth,
                I18N.getLocalizedString("invalidStructure.yearMonth.detail", yearMonth));
    }

    public static InputValidationIssue invalidEnterpriseNumber(InEnum in, String name, String enterpriseNumber) {
        return invalidStructure(in, name, enterpriseNumber,
                I18N.getLocalizedString("invalidStructure.enterpriseNumber.detail", enterpriseNumber));
    }

    public static InputValidationIssue invalidEstablishmentUnitNumber(InEnum in, String name,
            String establishmentUnitNumber) {
        return invalidStructure(in, name, establishmentUnitNumber,
                I18N.getLocalizedString("invalidStructure.establishmentUnitNumber.detail", establishmentUnitNumber));
    }

    public static InputValidationIssue exactlyOneOfExpected(List<Input<?>> inputs) {
        InputValidationIssue issue =
                invalidInputExt(ISSUE_TYPE_EXACTLY_ONE_OF_EXPECTED, "Exactly one of these inputs must be present")
                        .localizedDetail("exactlyOneOfExpected", getJoinedNames(inputs));
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(inputs);
        }
        return issue;
    }

    public static InputValidationIssue anyOfExpected(List<Input<?>> inputs) {
        InputValidationIssue issue =
                invalidInputExt(ISSUE_TYPE_ANY_OF_EXPECTED, "Any of these inputs must be present")
                        .localizedDetail("anyOfExpected", getJoinedNames(inputs));
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(inputs);
        }
        return issue;
    }

    public static InputValidationIssue zeroOrExactlyOneOfExpected(List<Input<?>> inputs) {
        InputValidationIssue issue = invalidInputExt(ISSUE_TYPE_ZERO_OR_EXACTLY_ONE_OF_EXPECTED,
                "Exactly one or none of these inputs must be present")
                        .localizedDetail("zeroOrExactlyOneOfExpected", getJoinedNames(inputs));
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(inputs);
        }
        return issue;
    }

    public static InputValidationIssue zeroOrAllOfExpected(List<Input<?>> inputs) {
        InputValidationIssue issue =
                invalidInputExt(ISSUE_TYPE_ZERO_OR_ALL_OF_EXPECTED, "All or none of these inputs must be present")
                        .localizedDetail("zeroOrAllOfExpected", getJoinedNames(inputs));
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(inputs);
        }
        return issue;
    }

    public static InputValidationIssue equalExpected(List<Input<?>> inputs) {
        InputValidationIssue issue =
                invalidInputExt(ISSUE_TYPE_EQUAL_EXPECTED, "These inputs must be equal")
                        .localizedDetail("equalExpected", getJoinedNames(inputs));
        if (ProblemConfig.isExtInputsArrayEnabled()) {
            issue.inputs(inputs);
        }
        return issue;
    }

    private static String getJoinedNames(List<Input<?>> inputs) {
        return inputs == null || inputs.isEmpty() ? ""
                : inputs.stream().map(Input::getName).collect(Collectors.joining(", "));
    }

    private static InputValidationIssue invalidInputExt(URI extIssueType, String extTitle) {
        if (!ProblemConfig.isExtIssueTypesEnabled()) {
            return new InputValidationIssue(ISSUE_TYPE_INVALID_INPUT, "Invalid input");
        } else {
            return new InputValidationIssue(extIssueType, extTitle);
        }
    }

}
