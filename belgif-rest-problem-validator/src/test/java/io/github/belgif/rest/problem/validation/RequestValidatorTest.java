package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RequestValidatorTest {

    @Test
    void ssinValid() {
        assertValid(new RequestValidator().ssin(Input.body("ssin", "00000000196")));
    }

    @Test
    void ssinNull() {
        assertValid(new RequestValidator().ssin(null));
        assertValid(new RequestValidator().ssin(Input.body("ssin", null)));
    }

    @Test
    void ssinInvalid() {
        assertInvalid(
                new RequestValidator().ssin(Input.body("ssin", "22222222222")),
                InputValidationIssues.invalidSsin(BODY, "ssin", "22222222222"));
    }

    @Test
    void ssinsInvalid() {
        assertInvalid(
                new RequestValidator().ssins(Input.body("ssins",
                        Arrays.asList("00000000196", "11111111111", "00000000295", "22222222222"))),
                InputValidationIssues.invalidSsin(BODY, "ssins[1]", "11111111111"),
                InputValidationIssues.invalidSsin(BODY, "ssins[3]", "22222222222"));
    }

    @Test
    void enterpriseNumberValid() {
        assertValid(new RequestValidator().enterpriseNumber(Input.body("enterpriseNumber", "0884303369")));
    }

    @Test
    void enterpriseNumberNull() {
        assertValid(new RequestValidator().enterpriseNumber(null));
        assertValid(new RequestValidator().enterpriseNumber(Input.body("enterpriseNumber", null)));
    }

    @Test
    void enterpriseNumberInvalid() {
        assertInvalid(
                new RequestValidator().enterpriseNumber(Input.body("enterpriseNumber", "2111111112")),
                InputValidationIssues.invalidEnterpriseNumber(BODY, "enterpriseNumber", "2111111112"));
    }

    @Test
    void establishmentUnitNumberValid() {
        assertValid(
                new RequestValidator().establishmentUnitNumber(Input.body("establishmentUnitNumber", "2297964444")));
    }

    @Test
    void establishmentUnitNumberNull() {
        assertValid(new RequestValidator().establishmentUnitNumber(null));
        assertValid(new RequestValidator().establishmentUnitNumber(Input.body("establishmentUnitNumber", null)));
    }

    @Test
    void establishmentUnitNumberInvalid() {
        assertInvalid(
                new RequestValidator().establishmentUnitNumber(Input.body("establishmentUnitNumber", "2111111111")),
                InputValidationIssues.invalidEstablishmentUnitNumber(BODY, "establishmentUnitNumber", "2111111111"));
    }

    @Test
    void periodValid() {
        InputPeriod period = new InputPeriod(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        assertValid(new RequestValidator().period(Input.body("period", period)));
    }

    @Test
    void periodNull() {
        assertValid(new RequestValidator().period(null));
        assertValid(new RequestValidator().period(Input.body("period", null)));
    }

    @Test
    void periodInvalid() {
        InputPeriod badPeriod = new InputPeriod(LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 12));
        assertInvalid(new RequestValidator().period(Input.body("period", badPeriod)),
                InputValidationIssues.invalidPeriod(BODY, "period", badPeriod));
    }

    @Test
    void temporalPeriodValid() {
        assertValid(new RequestValidator().period(Input.body("startDate", LocalDate.of(2023, 1, 1)),
                Input.body("endDate", LocalDate.of(2023, 12, 31))));
    }

    @Test
    void temporalPeriodInvalid() {
        assertInvalid(
                new RequestValidator().period(Input.body("startDate", LocalDate.of(2023, 10, 14)),
                        Input.body("endDate", LocalDate.of(2023, 10, 12))),
                InputValidationIssues.invalidPeriod(Input.body("startDate", LocalDate.of(2023, 10, 14)),
                        Input.body("endDate", LocalDate.of(2023, 10, 12))));
    }

    @Test
    void incompleteDateValid() {
        assertValid(new RequestValidator().incompleteDate(Input.body("date", "2024-00-00")));
    }

    @Test
    void incompleteDateNull() {
        assertValid(new RequestValidator().incompleteDate(null));
        assertValid(new RequestValidator().incompleteDate(Input.body("date", null)));
    }

    @Test
    void incompleteDateInvalid() {
        assertInvalid(
                new RequestValidator().incompleteDate(Input.body("date", "2024-00-01")),
                InputValidationIssues.invalidIncompleteDate(BODY, "date", "2024-00-01"));

    }

    @Test
    void yearMonthValid() {
        assertValid(new RequestValidator().yearMonth(Input.body("yearMonth", "2024-01")));
    }

    @Test
    void yearMonthNull() {
        assertValid(new RequestValidator().yearMonth(null));
        assertValid(new RequestValidator().yearMonth(Input.body("yearMonth", null)));
    }

    @Test
    void yearMonthInvalid() {
        assertInvalid(new RequestValidator().yearMonth(Input.body("yearMonth", "2024-99")),
                InputValidationIssues.invalidYearMonth(BODY, "yearMonth", "2024-99"));
    }

    @Test
    void exactlyOneOfValid() {
        Input<?>[] inputs = { Input.body("cbeNumber", null), Input.body("sector", "25") };
        assertValid(new RequestValidator().exactlyOneOf(inputs));
    }

    @Test
    void exactlyOneOfInvalidMoreThatOne() {
        Input<?>[] inputs = { Input.body("cbeNumber", "0694965804"), Input.body("sector", "25") };
        assertInvalid(new RequestValidator().exactlyOneOf(inputs),
                InputValidationIssues.exactlyOneOfExpected(Arrays.asList(inputs)));
    }

    @Test
    void exactlyOneOfInvalidNone() {
        Input<?>[] inputs = { Input.body("cbeNumber", null), Input.body("sector", null) };
        assertInvalid(new RequestValidator().exactlyOneOf(inputs),
                InputValidationIssues.exactlyOneOfExpected(Arrays.asList(inputs)));
    }

    @Test
    void anyOfValid() {
        Input<?>[] inputs = { Input.query("a", "value"), Input.query("b", null) };
        assertValid(new RequestValidator().anyOf(inputs));
    }

    @Test
    void anyOfInvalid() {
        Input<?>[] inputs = { Input.query("a", null), Input.query("b", null) };
        assertInvalid(new RequestValidator().anyOf(inputs),
                InputValidationIssues.anyOfExpected(Arrays.asList(inputs)));
    }

    @Test
    void zeroOrAllOfValidZero() {
        Input<?>[] inputs = { Input.query("a", null), Input.query("b", null) };
        assertValid(new RequestValidator().zeroOrAllOf(inputs));
    }

    @Test
    void zeroOrAllOfValidAll() {
        Input<?>[] inputs = { Input.query("a", "ok"), Input.query("b", "ok") };
        assertValid(new RequestValidator().zeroOrAllOf(inputs));
    }

    @Test
    void zeroOrAllOfInvalid() {
        Input<?>[] inputs = { Input.query("a", "ok"), Input.query("b", null) };
        assertInvalid(new RequestValidator().zeroOrAllOf(inputs),
                InputValidationIssues.zeroOrAllOfExpected(Arrays.asList(inputs)));
    }

    @Test
    void zeroOrExactlyOneOfValidZero() {
        Input<?>[] inputs = { Input.query("a", null), Input.query("b", null) };
        assertValid(new RequestValidator().zeroOrExactlyOneOf(inputs));
    }

    @Test
    void zeroOrExactlyOneOfValidOne() {
        Input<?>[] inputs = { Input.query("a", "ok"), Input.query("b", null) };
        assertValid(new RequestValidator().zeroOrExactlyOneOf(inputs));
    }

    @Test
    void zeroOrExactlyOneOfInvalid() {
        Input<?>[] inputs = { Input.query("a", "nok"), Input.query("b", "nok") };
        assertInvalid(new RequestValidator().zeroOrExactlyOneOf(inputs),
                InputValidationIssues.zeroOrExactlyOneOfExpected(Arrays.asList(inputs)));
    }

    @Test
    void equalValid() {
        Input<?>[] inputs = { Input.query("a", "ok"), Input.query("b", "ok") };
        assertValid(new RequestValidator().equal(inputs));
    }

    @Test
    void equalValidNull() {
        Input<?>[] inputs = { Input.query("a", null), Input.query("b", null) };
        assertValid(new RequestValidator().equal(inputs));
    }

    @Test
    void equalInvalid() {
        Input<?>[] inputs = { Input.query("a", "ok"), Input.query("b", "nok") };
        assertInvalid(new RequestValidator().equal(inputs),
                InputValidationIssues.equalExpected(Arrays.asList(inputs)));
    }

    @Test
    void nullOrEqualValidEqual() {
        assertValid(new RequestValidator().nullOrEqual(
                Input.query("a", "ok"), Input.query("b", "ok")));
    }

    @Test
    void nullOrEqualValidNull() {
        assertValid(new RequestValidator().nullOrEqual(
                Input.query("a", "ok"), Input.query("b", "ok")));
    }

    @Test
    void nullOrEqualInvalid() {
        assertInvalid(new RequestValidator().nullOrEqual(
                Input.query("a", "ok"), Input.query("b", "nok")),
                InputValidationIssues.equalExpected(Arrays.asList(Input.query("a", "ok"), Input.query("b", "nok"))));
    }

    @Test
    void refDataCollectionValid() {
        assertValid(new RequestValidator().refData(Input.query("refData", "b"), Arrays.asList("a", "b", "c")));
    }

    @Test
    void refDataCollectionInvalid() {
        assertInvalid(new RequestValidator().refData(Input.query("refData", "x"), Arrays.asList("a", "b", "c")),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x"));
    }

    @Test
    void refDataSupplierValid() {
        assertValid(new RequestValidator().refData(Input.query("refData", "b"), () -> Arrays.asList("a", "b", "c")));
    }

    @Test
    void refDataSupplierInvalid() {
        assertInvalid(new RequestValidator().refData(Input.query("refData", "x"), () -> Arrays.asList("a", "b", "c")),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x"));
    }

    @Test
    void refDataPredicateValid() {
        assertValid(
                new RequestValidator().refData(Input.query("refData", "b"), Arrays.asList("a", "b", "c")::contains));
    }

    @Test
    void refDataPredicateInvalid() {
        assertInvalid(
                new RequestValidator().refData(Input.query("refData", "x"), Arrays.asList("a", "b", "c")::contains),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "x"));
    }

    @Test
    void refDatasCollectionValid() {
        assertValid(new RequestValidator().refDatas(Input.query("refDatas",
                Arrays.asList("a", "b")), Arrays.asList("a", "b", "c")));
    }

    @Test
    void refDatasCollectionInvalid() {
        assertInvalid(new RequestValidator().refDatas(Input.query("refDatas",
                Arrays.asList("a", "x", "b", "y")), Arrays.asList("a", "b", "c")),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"));
    }

    @Test
    void refDatasSupplierValid() {
        AtomicInteger calls = new AtomicInteger(0);
        assertValid(new RequestValidator().refDatas(Input.query("refDatas",
                Arrays.asList("a", "b")), () -> {
                    calls.incrementAndGet();
                    return Arrays.asList("a", "b", "c");
                }));
        assertThat(calls).hasValue(1);
    }

    @Test
    void refDatasSupplierInvalid() {
        AtomicInteger calls = new AtomicInteger(0);
        assertInvalid(new RequestValidator().refDatas(Input.query("refDatas",
                Arrays.asList("a", "x", "b", "y")), () -> {
                    calls.incrementAndGet();
                    return Arrays.asList("a", "b", "c");
                }),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"));
        assertThat(calls).hasValue(1);
    }

    @Test
    void refDatasSupplierValidEmpty() {
        AtomicInteger calls = new AtomicInteger(0);
        assertValid(new RequestValidator().refDatas(Input.query("refDatas", Collections.emptyList()), () -> {
            calls.incrementAndGet();
            return Arrays.asList("a", "b", "c");
        }));
        assertThat(calls).hasValue(0);
    }

    @Test
    void refDatasPredicateValid() {
        assertValid(new RequestValidator().refDatas(Input.query("refDatas", Arrays.asList("a", "b")),
                Arrays.asList("a", "b", "c")::contains));
    }

    @Test
    void refDatasPredicateInvalid() {
        assertInvalid(new RequestValidator().refDatas(Input.query("refDatas", Arrays.asList("a", "x", "b", "y")),
                Arrays.asList("a", "b", "c")::contains),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[1]", "x"),
                InputValidationIssues.referencedResourceNotFound(QUERY, "refDatas[3]", "y"));
    }

    @Test
    void rejectValid() {
        assertValid(new RequestValidator().reject(Input.query("reject", null)));
    }

    @Test
    void rejectInvalid() {
        assertInvalid(new RequestValidator().reject(Input.query("reject", "nok")),
                InputValidationIssues.rejectedInput(QUERY, "reject", "nok"));
    }

    @Test
    void requireValid() {
        assertValid(new RequestValidator().require(Input.query("require", "ok")));
    }

    @Test
    void requireInvalid() {
        assertInvalid(new RequestValidator().require(Input.query("require", null)),
                InputValidationIssues.requiredInput(QUERY, "require"));
    }

    @Test
    void requireIfPresentValid() {
        assertValid(new RequestValidator().requireIfPresent(Input.query("target", "ok"),
                Input.query("a", "ok"), Input.query("b", "ok")));
        assertValid(new RequestValidator().requireIfPresent(Input.query("target", null),
                Input.query("a", "ok"), Input.query("b", "ok")));
        assertValid(new RequestValidator().requireIfPresent(Input.query("target", null),
                Input.query("a", null), Input.query("b", "ok")));
    }

    @Test
    void requireIfPresentInvalid() {
        assertInvalid(new RequestValidator().requireIfPresent(Input.query("target", "ok"),
                Input.query("a", null), Input.query("b", "ok")),
                InputValidationIssues.requiredInputsIfPresent(Input.query("target", "ok"),
                        Arrays.asList(Input.query("a", null), Input.query("b", "ok"))));
    }

    @Test
    void whenValid() {
        assertValid(new RequestValidator().when(false, validator -> validator.reject(Input.query("reject", "nok"))));
    }

    @Test
    void whenInvalid() {
        assertInvalid(new RequestValidator().when(true, validator -> validator.reject(Input.query("reject", "nok"))),
                InputValidationIssues.rejectedInput(QUERY, "reject", "nok"));
    }

    @Test
    void rangeValid() {
        assertValid(new RequestValidator().range(Input.query("page", 4), 1, 5));
    }

    @Test
    void rangeInvalid() {
        assertInvalid(new RequestValidator().range(Input.query("page", 6), 1, 5),
                InputValidationIssues.outOfRange(QUERY, "page", 6, 1, 5));
    }

    @Test
    void minimumValid() {
        assertValid(new RequestValidator().minimum(Input.query("page", 4), 1));
    }

    @Test
    void minimumInvalid() {
        assertInvalid(new RequestValidator().minimum(Input.query("page", 0), 1),
                InputValidationIssues.outOfRange(QUERY, "page", 0, 1, null));
    }

    @Test
    void maximumValid() {
        assertValid(new RequestValidator().maximum(Input.query("page", 4), 5));
    }

    @Test
    void maximumInvalid() {
        assertInvalid(new RequestValidator().maximum(Input.query("page", 6), 5),
                InputValidationIssues.outOfRange(QUERY, "page", 6, null, 5));
    }

    @Test
    void customValid() {
        assertValid(new RequestValidator().custom(Optional::empty));
    }

    @Test
    void customInvalid() {
        assertInvalid(new RequestValidator()
                .custom(() -> Optional.of(InputValidationIssues.invalidStructure(QUERY, "custom", "xyz", null))),
                InputValidationIssues.invalidStructure(QUERY, "custom", "xyz", null));
    }

    @Test
    void chainingValid() {
        assertValid(new RequestValidator().ssin(Input.body("ssin", "00000000196"))
                .enterpriseNumber(Input.body("enterpriseNumber", "0884303369")));
    }

    @Test
    void chainingInvalid() {
        assertInvalid(new RequestValidator().ssin(Input.body("ssin", "22222222222"))
                .enterpriseNumber(Input.body("enterpriseNumber", "2111111112")),
                InputValidationIssues.invalidSsin(BODY, "ssin", "22222222222"),
                InputValidationIssues.invalidEnterpriseNumber(BODY, "enterpriseNumber", "2111111112"));
    }

    @Test
    void extension() {
        final class MyRequestValidator extends AbstractRequestValidator<MyRequestValidator> implements
                StandardRequestValidatorModule<MyRequestValidator>,
                RequestValidatorModuleA<MyRequestValidator>,
                RequestValidatorModuleB<MyRequestValidator> {

            @Override
            public MyRequestValidator getThis() {
                return this;
            }
        }

        new MyRequestValidator()
                .require(Input.body("test", "value"))
                .b()
                .a()
                .validate();
        new MyRequestValidator()
                .a()
                .b()
                .require(Input.body("test", "value"))
                .validate();
    }

    private void assertValid(RequestValidator validator) {
        assertThatNoException().isThrownBy(validator::validate);
    }

    private void assertInvalid(RequestValidator validator, InputValidationIssue... issues) {
        assertThatExceptionOfType(BadRequestProblem.class).isThrownBy(validator::validate)
                .extracting(BadRequestProblem::getIssues).isEqualTo(Arrays.asList(issues));
    }

}
