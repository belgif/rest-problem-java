package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.belgif.rest.problem.config.ProblemConfig;

class InputValidationIssuesTest {

    public static Stream<Arguments> toggleExtIssueTypes() {
        return Stream.of(false, true).map(enabled -> {
            ProblemConfig.setExtIssueTypesEnabled(enabled);
            return Arguments.of(enabled);
        });
    }

    public static Stream<Arguments> toggleExtInputsArray() {
        return Stream.of(false, true).map(enabled -> {
            ProblemConfig.setExtInputsArrayEnabled(enabled);
            return Arguments.of(enabled);
        });
    }

    public static Stream<Arguments> toggleExtFeatures() {
        return Stream.of(false, true).flatMap(
                extIssueTypes -> Stream.of(false, true).map(extInputsArray -> {
                    ProblemConfig.setExtIssueTypesEnabled(extIssueTypes);
                    ProblemConfig.setExtInputsArrayEnabled(extInputsArray);
                    return Arguments.of(extIssueTypes, extInputsArray);
                }));
    }

    @AfterEach
    void resetProblemConfig() {
        ProblemConfig.reset();
    }

    @Test
    void schemaViolation() {
        InputValidationIssue issue =
                InputValidationIssues.schemaViolation(InEnum.BODY, "test", "value", "detail");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
        assertThat(issue.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/schemaViolation.html");
        assertThat(issue.getTitle()).isEqualTo("Input value is invalid with respect to the schema");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void unknownInput() {
        InputValidationIssue issue =
                InputValidationIssues.unknownInput(InEnum.BODY, "oops", "value");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:unknownInput");
        assertThat(issue.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/unknownInput.html");
        assertThat(issue.getTitle()).isEqualTo("Unknown input");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("oops");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Input oops is unknown");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void invalidInput() {
        InputValidationIssue issue =
                InputValidationIssues.invalidInput(InEnum.BODY, "oops", "value", "detail");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
        assertThat(issue.getHref())
                .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
        assertThat(issue.getTitle()).isEqualTo("Invalid input");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("oops");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidStructure(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidStructure(InEnum.BODY, "test", "value", "detail");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void outOfRangeMinMax(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 6, 1, 5);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:outOfRange");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/outOfRange.html");
            assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(6);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 6 is out of range [1, 5]");
        assertThat(issue.getAdditionalProperties()).containsOnly(entry("minimum", "1"), entry("maximum", "5"));
        assertThat(issue.getInputs()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void outOfRangeMin(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 0, 1, null);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:outOfRange");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/outOfRange.html");
            assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(0);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 0 should be at least 1");
        assertThat(issue.getAdditionalProperties()).containsExactly(entry("minimum", "1"));
        assertThat(issue.getInputs()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void outOfRangeMax(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.outOfRange(InEnum.BODY, "test", 6, null, 5);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:outOfRange");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/outOfRange.html");
            assertThat(issue.getTitle()).isEqualTo("Input value is out of range");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo(6);
        assertThat(issue.getDetail()).isEqualTo("Input value test = 6 should not exceed 5");
        assertThat(issue.getAdditionalProperties()).containsExactly(entry("maximum", "5"));
        assertThat(issue.getInputs()).isEmpty();
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
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:referencedResourceNotFound");
        assertThat(issue.getHref())
                .hasToString(
                        "https://www.belgif.be/specification/rest/api-guide/issues/referencedResourceNotFound.html");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Referenced resource test = 'value' does not exist");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void referencedResourceNotFoundDifferentResourceAndParameterName() {
        InputValidationIssue issue =
                InputValidationIssues.referencedResourceNotFound(InEnum.BODY, "partners", "organization", "0123456789");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:referencedResourceNotFound");
        assertThat(issue.getHref())
                .hasToString(
                        "https://www.belgif.be/specification/rest/api-guide/issues/referencedResourceNotFound.html");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("partners");
        assertThat(issue.getValue()).isEqualTo("0123456789");
        assertThat(issue.getDetail()).isEqualTo("Referenced resource organization = '0123456789' does not exist");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void referencedResourceFromCollectionParameterNotFound() {
        InputValidationIssue issue =
                InputValidationIssues.referencedResourceNotFound(InEnum.BODY, "partners", 123,
                        Arrays.asList(1, 123, 3));
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:referencedResourceNotFound");
        assertThat(issue.getHref())
                .hasToString(
                        "https://www.belgif.be/specification/rest/api-guide/issues/referencedResourceNotFound.html");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("partners[1]");
        assertThat(issue.getValue()).isEqualTo(123);
        assertThat(issue.getDetail()).isEqualTo("Referenced resource partners[1] = '123' does not exist");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void rejectedInput(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.rejectedInput(InEnum.BODY, "test", "value");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:rejectedInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/rejectedInput.html");
            assertThat(issue.getTitle()).isEqualTo("Input is not allowed in this context");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("Input test is not allowed in this context");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void requiredInput(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.requiredInput(InEnum.BODY, "test");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:requiredInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/requiredInput.html");
            assertThat(issue.getTitle()).isEqualTo("Input is required in this context");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("Input test is required in this context");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void requiredInputsIfPresent(boolean extIssueTypes, boolean extInputsArray) {
        Input<?> target = Input.query("x", "value");
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.requiredInputsIfPresent(target, inputs);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:requiredInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/requiredInput.html");
            assertThat(issue.getTitle()).isEqualTo("Input is required in this context");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getDetail()).isEqualTo("All of these inputs must be present if x is present: a, b");
        if (extInputsArray) {
            assertThat(issue.getInputs()).contains(target).containsAll(inputs);
            assertThat(issue).extracting("in", "name", "value").allMatch(this::isEmpty);
        } else {
            assertThat(issue.getInputs()).isEmpty();
            assertThat(issue.getIn()).isEqualTo(target.getIn());
            assertThat(issue.getName()).isEqualTo(target.getName());
            assertThat(issue.getValue()).isEqualTo(target.getValue());
        }
        assertThat(issue.getAdditionalProperties()).isEmpty();
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
        assertThat(issue.getInputs()).extracting("href", "inputs").allMatch(this::isEmpty);
    }

    @Test
    void replacedSsinInput() {
        InputValidationIssue issue =
                InputValidationIssues.replacedSsin(Input.body("ssin", "00000000196"), "00000000295");
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
    void replacedSsinWithReplacedByHref() {
        InputValidationIssue issue =
                InputValidationIssues.replacedSsin(InEnum.BODY, "ssin", "00000000196", "00000000295",
                        URI.create("https://api.company.com/v1/employees?ssin=00000000295"));
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:replacedSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN has been replaced, use new SSIN");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 has been replaced by 00000000295");
        assertThat(issue.getAdditionalProperties()).containsExactly(
                entry("replacedBy", "00000000295"),
                entry("replacedByHref", "https://api.company.com/v1/employees?ssin=00000000295"));
        assertThat(issue).extracting("href", "inputs").allMatch(this::isEmpty);
    }

    @Test
    void canceledSsin() {
        InputValidationIssue issue = InputValidationIssues.canceledSsin(InEnum.BODY, "ssin", "00000000196");
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:canceledSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN has been canceled");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 has been canceled");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void canceledSsinInput() {
        InputValidationIssue issue = InputValidationIssues.canceledSsin(Input.body("ssin", "00000000196"));
        assertThat(issue.getType()).hasToString("urn:problem-type:cbss:input-validation:canceledSsin");
        assertThat(issue.getTitle()).isEqualTo("SSIN has been canceled");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 has been canceled");
        assertThat(issue).extracting("href", "inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidSsin(boolean extIssueTypes) {
        InputValidationIssue issue = InputValidationIssues.invalidSsin(InEnum.BODY, "ssin", "00000000195");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000195");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000195 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidSsinInput(boolean extIssueTypes) {
        InputValidationIssue issue = InputValidationIssues.invalidSsin(Input.body("ssin", "00000000195"));
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000195");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000195 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void unknownSsin() {
        InputValidationIssue issue = InputValidationIssues.unknownSsin(InEnum.BODY, "ssin", "00000000196");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:referencedResourceNotFound");
        assertThat(issue.getHref()).hasToString(
                "https://www.belgif.be/specification/rest/api-guide/issues/referencedResourceNotFound.html");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 does not exist");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @Test
    void unknownSsinInput() {
        InputValidationIssue issue = InputValidationIssues.unknownSsin(Input.body("ssin", "00000000196"));
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:referencedResourceNotFound");
        assertThat(issue.getHref()).hasToString(
                "https://www.belgif.be/specification/rest/api-guide/issues/referencedResourceNotFound.html");
        assertThat(issue.getTitle()).isEqualTo("Referenced resource not found");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("ssin");
        assertThat(issue.getValue()).isEqualTo("00000000196");
        assertThat(issue.getDetail()).isEqualTo("SSIN 00000000196 does not exist");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidPeriod(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidPeriod(InEnum.BODY, "period", "value");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidPeriod");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidPeriod.html");
            assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("period");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("endDate should not precede startDate");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void invalidPeriodLocalDate(boolean extIssueTypes, boolean extInputsArray) {
        Input<LocalDate> startDate = Input.query("startDate", LocalDate.of(2024, 1, 1));
        Input<LocalDate> endDate = Input.query("endDate", LocalDate.of(2023, 1, 1));
        InputValidationIssue issue = InputValidationIssues.invalidPeriod(startDate, endDate);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidPeriod");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidPeriod.html");
            assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).containsExactly(startDate, endDate);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("endDate should not precede startDate");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void invalidPeriodOffsetDateTime(boolean extIssueTypes, boolean extInputsArray) {
        Input<OffsetDateTime> startDateTime = Input.query("startDateTime",
                LocalDate.of(2024, 1, 1).atTime(OffsetTime.MAX));
        Input<OffsetDateTime> endDateTime = Input.query("endDateTime",
                LocalDate.of(2023, 1, 1).atTime(OffsetTime.MIN));
        InputValidationIssue issue = InputValidationIssues.invalidPeriod(startDateTime, endDateTime);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidPeriod");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidPeriod.html");
            assertThat(issue.getTitle()).isEqualTo("Period is invalid");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).containsExactly(startDateTime, endDateTime);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("endDateTime should not precede startDateTime");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidIncompleteDate(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidIncompleteDate(InEnum.BODY, "test", "2024-00-01");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("2024-00-01");
        assertThat(issue.getDetail()).isEqualTo("Incomplete date 2024-00-01 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidYearMonth(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidYearMonth(InEnum.BODY, "test", "2024-13");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("2024-13");
        assertThat(issue.getDetail()).isEqualTo("Year month 2024-13 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidEnterpriseNumber(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidEnterpriseNumber(InEnum.BODY, "test", "0000000001");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("0000000001");
        assertThat(issue.getDetail()).isEqualTo("Enterprise number 0000000001 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtIssueTypes")
    void invalidEstablishmentUnitNumber(boolean extIssueTypes) {
        InputValidationIssue issue =
                InputValidationIssues.invalidEstablishmentUnitNumber(InEnum.BODY, "test", "0000000001");
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:invalidStructure");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/invalidStructure.html");
            assertThat(issue.getTitle()).isEqualTo("Input value has invalid structure");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("test");
        assertThat(issue.getValue()).isEqualTo("0000000001");
        assertThat(issue.getDetail()).isEqualTo("Establishment unit number 0000000001 is invalid");
        assertThat(issue).extracting("inputs", "additionalProperties").allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void exactlyOneOfExpected(boolean extIssueTypes, boolean extInputsArray) {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "value"), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.exactlyOneOfExpected(inputs);
        if (extIssueTypes) {
            assertThat(issue.getType())
                    .hasToString("urn:problem-type:belgif-ext:input-validation:exactlyOneOfExpected");
            assertThat(issue.getHref()).hasToString(
                    "https://www.belgif.be/specification/rest/api-guide/issues/ext/exactlyOneOfExpected.html");
            assertThat(issue.getTitle()).isEqualTo("Exactly one of these inputs must be present");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).isEqualTo(inputs);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("Exactly one of these inputs must be present: a, b");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void anyOfExpected(boolean extIssueTypes, boolean extInputsArray) {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", null));
        InputValidationIssue issue = InputValidationIssues.anyOfExpected(inputs);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:anyOfExpected");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/anyOfExpected.html");
            assertThat(issue.getTitle()).isEqualTo("Any of these inputs must be present");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).isEqualTo(inputs);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("Any of these inputs must be present: a, b");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void zeroOrExactlyOneOfExpected(boolean extIssueTypes, boolean extInputsArray) {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "value"), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.zeroOrExactlyOneOfExpected(inputs);
        if (extIssueTypes) {
            assertThat(issue.getType())
                    .hasToString("urn:problem-type:belgif-ext:input-validation:zeroOrExactlyOneOfExpected");
            assertThat(issue.getHref()).hasToString(
                    "https://www.belgif.be/specification/rest/api-guide/issues/ext/zeroOrExactlyOneOfExpected.html");
            assertThat(issue.getTitle()).isEqualTo("Exactly one or none of these inputs must be present");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).isEqualTo(inputs);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("Exactly one or none of these inputs must be present: a, b");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void zeroOrAllOfExpected(boolean extIssueTypes, boolean extInputsArray) {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", "value"));
        InputValidationIssue issue = InputValidationIssues.zeroOrAllOfExpected(inputs);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:zeroOrAllOfExpected");
            assertThat(issue.getHref()).hasToString(
                    "https://www.belgif.be/specification/rest/api-guide/issues/ext/zeroOrAllOfExpected.html");
            assertThat(issue.getTitle()).isEqualTo("All or none of these inputs must be present");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).isEqualTo(inputs);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("All or none of these inputs must be present: a, b");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    @ParameterizedTest
    @MethodSource("toggleExtFeatures")
    void equalExpected(boolean extIssueTypes, boolean extInputsArray) {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "this"), Input.query("b", "that"));
        InputValidationIssue issue = InputValidationIssues.equalExpected(inputs);
        if (extIssueTypes) {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif-ext:input-validation:equalExpected");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/ext/equalExpected.html");
            assertThat(issue.getTitle()).isEqualTo("These inputs must be equal");
        } else {
            assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:invalidInput");
            assertThat(issue.getHref())
                    .hasToString("https://www.belgif.be/specification/rest/api-guide/issues/invalidInput.html");
            assertThat(issue.getTitle()).isEqualTo("Invalid input");
        }
        if (extInputsArray) {
            assertThat(issue.getInputs()).isEqualTo(inputs);
        } else {
            assertThat(issue.getInputs()).isEmpty();
        }
        assertThat(issue.getDetail()).isEqualTo("These inputs must be equal: a, b");
        assertThat(issue).extracting("in", "name", "value", "additionalProperties")
                .allMatch(this::isEmpty);
    }

    public boolean isEmpty(Object value) {
        return value == null || Integer.valueOf(0).equals(value) || Collections.emptyList().equals(value)
                || Collections.emptyMap().equals(value);
    }

}
