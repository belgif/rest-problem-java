package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class InputValidationIssuesTest {

    @Test
    void schemaViolation() {
        InputValidationIssue issue =
                InputValidationIssues.schemaViolation(InEnum.BODY, "test", "value", "detail");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
        assertThat(issue.getTitle()).isEqualTo("Input value is invalid with respect to the schema");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void unknownInput() {
        InputValidationIssue issue =
                InputValidationIssues.unknownInput(InEnum.BODY, "oops", "value");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:unknownInput");
        assertThat(issue.getTitle()).isEqualTo("Unknown input");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("oops");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Input oops is unknown");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidStructure() {
        InputValidationIssue issue =
                InputValidationIssues.invalidStructure(InEnum.BODY, "test", "value", "detail");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void outOfRangeMinMax() {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 6, 1, 5);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:outOfRange");
        assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(6);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 6 is out of range [1, 5]");
        assertThat(issue.getAdditionalProperties()).containsOnly(entry("minimum", "1"), entry("maximum", "5"));
        assertThat(issue).extracting("href", "inputs").allMatch(this::isEmpty);

    }

    @Test
    void outOfRangeMin() {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 0, 1, null);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:outOfRange");
        assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(0);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 0 should be at least 1");
        assertThat(issue.getAdditionalProperties()).containsExactly(entry("minimum", "1"));
        assertThat(issue).extracting("href", "inputs").allMatch(this::isEmpty);
    }

    @Test
    void outOfRangeMax() {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 6, null, 5);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:outOfRange");
        assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(6);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 6 should not exceed 5");
        assertThat(issue.getAdditionalProperties()).containsExactly(entry("maximum", "5"));
        assertThat(issue).extracting("href", "inputs").allMatch(this::isEmpty);
    }

    @Test
    void outOfRangeMinAndMaxNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> InputValidationIssues.outOfRange(InEnum.BODY, "test", 6, null, null))
                .withMessage("At least one of min, max must be non-null");
    }

    @Test
    void referencedResourceNotFound() {
        InputValidationIssue issue =
                InputValidationIssues.referencedResourceNotFound(InEnum.BODY, "test", "value");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:referencedResourceNotFound");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Referenced resource test = 'value' does not exist");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void rejectedInput() {
        InputValidationIssue issue =
                InputValidationIssues.rejectedInput(InEnum.BODY, "test", "value");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:rejectedInput");
        assertThat(issue.getTitle()).isEqualTo("Input is not allowed in this context");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Input test is not allowed in this context");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void requiredInput() {
        InputValidationIssue issue =
                InputValidationIssues.requiredInput(InEnum.BODY, "test");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:requiredInput");
        assertThat(issue.getTitle()).isEqualTo("Input is required in this context");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("Input test is required in this context");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void requiredInputsIfPresent() {
        Input<?> target = Input.query("x", "value");
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.requiredInputsIfPresent(target, inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:requiredInput");
        assertThat(issue.getTitle()).isEqualTo("Input is required in this context");
        assertThat(issue.getDetail()).isEqualTo("All of these inputs must be present if x is present: a, b");
        assertThat(issue.getInputs()).contains(target).containsAll(inputs);
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void replacedSsin() {
        InputValidationIssue issue =
                InputValidationIssues.replacedSsin(InEnum.BODY, "ssin", "00000000196", "00000000295");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:replacedSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN has been replaced, use new SSIN");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 has been replaced by 00000000295");
        assertThat(issue.getAdditionalProperties()).containsExactly(entry("replacedBy", "00000000295"));
        assertThat(issue).extracting("href", "inputs").allMatch(this::isEmpty);
    }

    @Test
    void canceledSsin() {
        InputValidationIssue issue =
                InputValidationIssues.canceledSsin(InEnum.BODY, "ssin", "00000000196");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:canceledSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN has been canceled");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 has been canceled");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidSsin() {
        InputValidationIssue issue =
                InputValidationIssues.invalidSsin(InEnum.BODY, "ssin", "00000000195");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000195");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000195 is invalid");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void unknownSsin() {
        InputValidationIssue issue =
                InputValidationIssues.unknownSsin(InEnum.BODY, "ssin", "00000000196");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:unknownSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN does not exist");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 does not exist");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidPeriod() {
        InputValidationIssue issue =
                InputValidationIssues.invalidPeriod(InEnum.BODY, "period", "value");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidPeriod");
        assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("period");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("endDate should not precede startDate");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidPeriodLocalDate() {
        Input<LocalDate> startDate = Input.query("startDate", LocalDate.of(2024, 1, 1));
        Input<LocalDate> endDate = Input.query("endDate", LocalDate.of(2023, 1, 1));
        InputValidationIssue issue = InputValidationIssues.invalidPeriod(startDate, endDate);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidPeriod");
        assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        assertThat(issue.getInputs()).containsExactly(startDate, endDate);
        assertThat(issue.getDetail()).isEqualTo("endDate should not precede startDate");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void invalidPeriodOffsetDateTime() {
        Input<OffsetDateTime> startDateTime = Input.query("startDateTime",
                LocalDate.of(2024, 1, 1).atTime(OffsetTime.MAX));
        Input<OffsetDateTime> endDateTime = Input.query("endDateTime",
                LocalDate.of(2023, 1, 1).atTime(OffsetTime.MIN));
        InputValidationIssue issue = InputValidationIssues.invalidPeriod(startDateTime, endDateTime);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidPeriod");
        assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        assertThat(issue.getInputs()).containsExactly(startDateTime, endDateTime);
        assertThat(issue.getDetail()).isEqualTo("endDateTime should not precede startDateTime");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void invalidIncompleteDate() {
        InputValidationIssue issue =
                InputValidationIssues.invalidIncompleteDate(InEnum.BODY, "test", "2024-00-01");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("2024-00-01");
        assertThat(issue.getDetail()).isEqualTo("Incomplete date 2024-00-01 is invalid");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidYearMonth() {
        InputValidationIssue issue =
                InputValidationIssues.invalidYearMonth(InEnum.BODY, "test", "2024-13");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("2024-13");
        assertThat(issue.getDetail()).isEqualTo("Year month 2024-13 is invalid");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidEnterpriseNumber() {
        InputValidationIssue issue =
                InputValidationIssues.invalidEnterpriseNumber(InEnum.BODY, "test", "0000000001");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("0000000001");
        assertThat(issue.getDetail()).isEqualTo("Enterprise number 0000000001 is invalid");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidEstablishmentUnitNumber() {
        InputValidationIssue issue =
                InputValidationIssues.invalidEstablishmentUnitNumber(InEnum.BODY, "test", "0000000001");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:invalidStructure");
        assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("0000000001");
        assertThat(issue.getDetail()).isEqualTo("Establishment unit number 0000000001 is invalid");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void exactlyOneOfExpected() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "value"), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.exactlyOneOfExpected(inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:exactlyOneOfExpected");
        assertThat(issue.getTitle()).isEqualTo("Exactly one of these inputs must be present");
        assertThat(issue.getInputs()).isEqualTo(inputs);
        assertThat(issue.getDetail()).isEqualTo("Exactly one of these inputs must be present: a, b");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void anyOfExpected() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", null));
        InputValidationIssue issue = InputValidationIssues.anyOfExpected(inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:anyOfExpected");
        assertThat(issue.getTitle()).isEqualTo("Any of these inputs must be present");
        assertThat(issue.getInputs()).isEqualTo(inputs);
        assertThat(issue.getDetail()).isEqualTo("Any of these inputs must be present: a, b");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void zeroOrExactlyOneOfExpected() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "value"), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.zeroOrExactlyOneOfExpected(inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:zeroOrExactlyOneOfExpected");
        assertThat(issue.getTitle()).isEqualTo("Exactly one or none of these inputs must be present");
        assertThat(issue.getInputs()).isEqualTo(inputs);
        assertThat(issue.getDetail()).isEqualTo("Exactly one or none of these inputs must be present: a, b");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void zeroOrAllOfExpected() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.zeroOrAllOfExpected(inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:zeroOrAllOfExpected");
        assertThat(issue.getTitle()).isEqualTo("All or none of these inputs must be present");
        assertThat(issue.getInputs()).isEqualTo(inputs);
        assertThat(issue.getDetail()).isEqualTo("All or none of these inputs must be present: a, b");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @Test
    void equalExpected() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "this"), Input.query("b", "that"));
        InputValidationIssue issue = InputValidationIssues.equalExpected(inputs);
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:equalExpected");
        assertThat(issue.getTitle()).isEqualTo("These inputs must be equal");
        assertThat(issue.getInputs()).isEqualTo(inputs);
        assertThat(issue.getDetail()).isEqualTo("These inputs must be equal: a, b");
        assertThat(issue).extracting("href", "in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    public boolean isEmpty(Object value) {
        return value == null || Integer.valueOf(0).equals(value) || Collections.emptyList().equals(value)
                || Collections.emptyMap().equals(value);
    }

}
