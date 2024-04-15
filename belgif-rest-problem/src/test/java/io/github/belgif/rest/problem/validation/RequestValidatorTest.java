package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RequestValidatorTest {
    private RequestValidator tested;

    @BeforeEach
    void init() {
        tested = new RequestValidator();
    }

    @Test
    void validateAllOk() {
        tested.ssin(Input.body("criteria.ssin", "23292805780"));

        tested.enterpriseNumber(Input.body("criteria.enterpriseNumber", "0694965804"));

        tested.establishmentUnitNumber(Input.body("criteria.establishmentUnitNumber", "2275037604"));

        tested.period(Input.body("criteria.periods.period", new InputPeriod(null, LocalDate.of(2023, 10, 12))));
        tested.period(Input.body("criteria.periods.start", null),
                Input.body("criteria.periods.end", LocalDate.of(2023, 10, 12)));

        tested.incompleteDate(Input.body("criteria.date", "2024-01-00"));
        tested.yearMonth(Input.body("criteria.month", "2024-01"));

        tested.exactlyOneOf(Input.body("cbeNumber", null), Input.body("sector", "25"));

        tested.equal(Input.header("id", "25"), Input.body("id", "25"));

        tested.nullOrEqual(Input.header("id", "25"), Input.body("id", "25"));
        tested.nullOrEqual(Input.header("id", null), Input.body("id", "25"));

        tested.anyOf(Input.header("id", "25"), Input.body("id1", null));

        tested.zeroOrAllOf(Input.body("cbeNumber", null), Input.body("sector", null));

        tested.zeroOrExactlyOneOf(Input.body("cbeNumber", null), Input.body("sector", null));

        tested.refData(Input.query("refData", "a"), Arrays.asList("a", "b", "c"));
        tested.refData(Input.query("refData", "a"), "A"::equalsIgnoreCase);

        tested.reject(Input.body("reject", null));
        tested.require(Input.body("required", "ok"));

        assertDoesNotThrow(() -> tested.validate());
    }

    @Test
    void validateNullOk() {
        tested.ssin(Input.body("criteria.ssin", null));
        tested.enterpriseNumber(Input.body("criteria.enterpriseNumber", null));
        tested.establishmentUnitNumber(Input.body("criteria.establishmentUnitNumber", null));
        tested.period(Input.body("criteria.periods.period", null));
        tested.incompleteDate(Input.body("criteria.date", null));
        tested.yearMonth(Input.body("criteria.month", null));
        tested.refData(Input.body("criteria.refData", null), Arrays.asList("A", "B"));
        tested.refData(Input.body("criteria.refData", null), "A"::equals);
        assertDoesNotThrow(() -> tested.validate());
    }

    @Test
    void validateSsinNok() {
        tested.ssin(Input.body("criteria.ssin", "22222222222"));
        verifyValidateWithError(
                new BadRequestProblem(InputValidationIssues.invalidSsin(BODY, "criteria.ssin", "22222222222")));
    }

    @Test
    void validateSsinsNok() {
        tested.ssins(Input.body("criteria.ssins",
                Arrays.asList("00000000196", "11111111111", "00000000295", "22222222222")));
        verifyValidateWithError(new BadRequestProblem(Arrays.asList(
                InputValidationIssues.invalidSsin(BODY, "criteria.ssins[1]", "11111111111"),
                InputValidationIssues.invalidSsin(BODY, "criteria.ssins[3]", "22222222222"))));
    }

    @Test
    void validateEnterpriseNumberNOk() {
        tested.enterpriseNumber(Input.body("criteria.enterpriseNumber", "2111111112"));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.invalidEnterpriseNumber(BODY, "criteria.enterpriseNumber",
                        "2111111112")));
    }

    @Test
    void validateEstablishmentUnitNumberNOk() {
        tested.establishmentUnitNumber(Input.body("criteria.establishmentUnitNumber", "2111111111"));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.invalidEstablishmentUnitNumber(BODY, "criteria.establishmentUnitNumber",
                        "2111111111")));
    }

    @Test
    void validatePeriodNOk() {
        InputPeriod badPeriod = new InputPeriod(LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 12));
        tested.period(Input.body("criteria.periods.period", badPeriod));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.invalidPeriod(BODY, "criteria.periods.period", badPeriod)));
    }

    @Test
    void validateIncompleteDateNOk() {
        tested.incompleteDate(Input.body("criteria.date", "2024-00-01"));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2024-00-01")));
    }

    @Test
    void validateYearMonthNOk() {
        tested.yearMonth(Input.body("criteria.month", "2024-99"));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.invalidYearMonth(BODY, "criteria.month", "2024-99")));
    }

    @Test
    void validateExactlyOneOfNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        tested.exactlyOneOf(Input.body("cbeNumber", "0694965804"), Input.body("sector", "25"));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.exactlyOneOfExpected(items)));
    }

    @Test
    void validateEqualNOk() {
        List<Input<?>> items = Arrays.asList(Input.header("id", "25"),
                Input.body("id", "26"));
        tested.equal(Input.header("id", "25"), Input.body("id", "26"));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.equalExpected(items)));
    }

    @Test
    void validateNullOrEqualNOk() {
        List<Input<?>> items = Arrays.asList(Input.header("id", "25"),
                Input.body("id", "26"));
        tested.nullOrEqual(Input.header("id", "25"), Input.body("id", "26"));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.equalExpected(items)));
    }

    @Test
    void validateAnyOfNOk() {
        List<Input<?>> items =
                Arrays.asList(Input.header("id", null), Input.body("id1", null));
        tested.anyOf(Input.header("id", null), Input.body("id1", null));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.anyOfExpected(items)));
    }

    @Test
    void validateZeroOrAllOfNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", null));
        tested.zeroOrAllOf(Input.body("cbeNumber", "0694965804"), Input.body("sector", null));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.zeroOrAllOfExpected(items)));
    }

    @Test
    void validateZeroOrExactlyOneOfNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        tested.zeroOrExactlyOneOf(Input.body("cbeNumber", "0694965804"), Input.body("sector", "25"));
        verifyValidateWithError(new BadRequestProblem(InputValidationIssues.zeroOrExactlyOneOfExpected(items)));
    }

    @Test
    void validateRefDataNok() {
        tested.refData(Input.query("refData", "x"), Arrays.asList("a", "b", "c"));
        verifyValidateWithError(
                new BadRequestProblem(InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x")));
    }

    @Test
    void validateRefDataNokSupplier() {
        tested.refData(Input.query("refData", "x"), () -> Arrays.asList("a", "b", "c"));
        verifyValidateWithError(
                new BadRequestProblem(InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x")));
    }

    @Test
    void validateRefDataNokPredicate() {
        tested.refData(Input.query("refData", "x"), Arrays.asList("a", "b", "c")::contains);
        verifyValidateWithError(
                new BadRequestProblem(InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x")));
    }

    @Test
    void validateRefDatasNok() {
        tested.refDatas(Input.query("refDatas", Arrays.asList("a", "x", "b", "y")), Arrays.asList("a", "b", "c"));
        verifyValidateWithError(new BadRequestProblem(Arrays.asList(
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"))));
    }

    @Test
    void validateRefDatasNokSupplier() {
        AtomicInteger calls = new AtomicInteger(0);
        tested.refDatas(Input.query("refDatas", Arrays.asList("a", "x", "b", "y")), () -> {
            calls.incrementAndGet();
            return Arrays.asList("a", "b", "c");
        });
        verifyValidateWithError(new BadRequestProblem(Arrays.asList(
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"))));
        assertThat(calls).hasValue(1);
    }

    @Test
    void validateRefDatasNokSupplierEmpty() {
        AtomicInteger calls = new AtomicInteger(0);
        tested.refDatas(Input.query("refDatas", Collections.emptyList()), () -> {
            calls.incrementAndGet();
            return Arrays.asList("a", "b", "c");
        });
        tested.validate();
        assertThat(calls).hasValue(0);
    }

    @Test
    void validateRefDatasNokPredicate() {
        tested.refDatas(Input.query("refDatas", Arrays.asList("a", "x", "b", "y")),
                Arrays.asList("a", "b", "c")::contains);
        verifyValidateWithError(new BadRequestProblem(Arrays.asList(
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"))));
    }

    @Test
    void validateRejectedInput() {
        tested.reject(Input.body("reject", "bad"));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.rejectedInput(BODY, "reject", "bad")));
    }

    @Test
    void validateRequiredInput() {
        tested.require(Input.body("required", null));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.requiredInput(BODY, "required", null)));
    }

    @Test
    void validateWhenConditionalFalse() {
        tested.when(false, validator -> validator.reject(Input.body("reject", "bad")));
        assertDoesNotThrow(() -> tested.validate());
    }

    @Test
    void validateWhenConditionalTrue() {
        tested.when(true, validator -> validator.reject(Input.body("reject", "bad")));
        verifyValidateWithError(new BadRequestProblem(
                InputValidationIssues.rejectedInput(BODY, "reject", "bad")));
    }

    @Test
    void validateMultipleIssues() {
        List<Input<?>> itemsExactlyOne = Arrays.asList(Input.body("id", "25"),
                Input.body("id1", "22"));
        InputValidationIssue issue1 = InputValidationIssues.exactlyOneOfExpected(itemsExactlyOne);
        tested.exactlyOneOf(Input.body("id", "25"), Input.body("id1", "22"));

        List<Input<?>> itemsZeroOrOne = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        InputValidationIssue issue2 = InputValidationIssues.zeroOrExactlyOneOfExpected(itemsZeroOrOne);
        tested.zeroOrExactlyOneOf(Input.body("cbeNumber", "0694965804"), Input.body("sector", "25"));

        List<Input<?>> itemsAtLeastOne = Arrays.asList(Input.header("id", null), Input.body("id1", null));
        InputValidationIssue issue3 = InputValidationIssues
                .anyOfExpected(itemsAtLeastOne);
        tested.anyOf(Input.header("id", null), Input.body("id1", null));

        verifyValidateWithError(new BadRequestProblem(Arrays.asList(issue1, issue2, issue3)));
    }

    private void verifyValidateWithError(BadRequestProblem expected) {
        BadRequestProblem thrown =
                assertThrows(BadRequestProblem.class, () -> tested.validate(), "Should have thrown exception");
        assertThat(thrown).usingRecursiveComparison().isEqualTo(expected);
    }
}
