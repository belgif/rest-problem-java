package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class SsinValidatorTest {

    private SsinValidator tested;

    @Test
    void validateOk() {
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "23292805780"));
        assertThat(tested.validate()).isEmpty();

        // day== 0
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81050003552"));
        assertThat(tested.validate()).isEmpty();

    }

    @Test
    void validateBisOk() {

        // bis with month == 20
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "92200006963"));
        assertThat(tested.validate()).isEmpty();

        // bis with month == 32
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "92321506985"));
        assertThat(tested.validate()).isEmpty();

        // bis with month == 40
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "92400006909"));
        assertThat(tested.validate()).isEmpty();

        // bis with month == 52
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "92521506931"));
        assertThat(tested.validate()).isEmpty();

        // bis with month >20 & <32
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "70252501507"));
        assertThat(tested.validate()).isEmpty();

        // bis with month >40 & <52
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "92471506993"));
        assertThat(tested.validate()).isEmpty();

        // bis 2k
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "19282416134"));
        assertThat(tested.validate()).isEmpty();

        // counter == 999 with BIS
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "80240199943"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNrOk() {
        // nr with month == 0
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81000003517"));
        assertThat(tested.validate()).isEmpty();

        // nr with month == 12
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81120103571"));
        assertThat(tested.validate()).isEmpty();

        // nr with month > 0 && month < 12
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81050103522"));
        assertThat(tested.validate()).isEmpty();

        // nr 2k
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "19082416188"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateBirthDateOk() {
        // birth date is today
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", createSsinFromDate(LocalDate.now())));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateBirthDateNOk() {
        String ssin = createSsinFromDate(LocalDate.now().plusDays(1));
        // birth date is today
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", ssin));
        InputValidationIssue expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", ssin);
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new SsinValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        Input input = new Input<>(null, "ssin", "23292805780");
        assertThrows(NullPointerException.class, () -> new SsinValidator(input), "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        Input input = new Input<>(null, "ssin", "23292805780");
        assertThrows(NullPointerException.class, () -> new SsinValidator(input), "Should have thrown exception");
    }

    @Test
    void validateNOk() {

        // invalid ssin
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "11111111111"));
        InputValidationIssue expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "11111111111");
        assertThat(tested.validate()).contains(expected);

        // too long string value
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "810501035220"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "810501035220");
        assertThat(tested.validate()).contains(expected);

        // negative number
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "-8105010352"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "-8105010352");
        assertThat(tested.validate()).contains(expected);

        // month out of scope
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81130103578"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "81130103578");
        assertThat(tested.validate()).contains(expected);

        // day out of scope
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81043103522"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "81043103522");
        assertThat(tested.validate()).contains(expected);

        // day out of scope for month
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81023003522"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "81023003522");
        assertThat(tested.validate()).contains(expected);

        // counter == 0 with nr
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "47060200096"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "47060200096");
        assertThat(tested.validate()).contains(expected);

        // counter == 999 with nr
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "99020299964"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "99020299964");
        assertThat(tested.validate()).contains(expected);

        // invalid checksum
        tested = new SsinValidator(Input.body("criteria.ssins.ssin", "81050103521"));
        expected = InputValidationIssues.invalidSsin(BODY, "criteria.ssins.ssin", "81050103521");
        assertThat(tested.validate()).contains(expected);

    }

    private String createSsinFromDate(LocalDate date) {
        int yearPart = Integer.parseInt(String.valueOf(date.getYear()).substring(2));
        int monthPart = date.getMonthValue();
        int dayPart = date.getDayOfMonth();
        int counterPart = 1;

        long radix = (yearPart * 10000000L) + (monthPart * 100000L) + (dayPart * 1000L) + counterPart;
        int checksum = calculate2KChecksum(radix);
        long ssin = (radix * 100) + checksum;

        return String.format("%011d", ssin);
    }

    private static int calculate2KChecksum(long radix) {
        return (int) (97L - ((2000000000 + radix) % 97L));
    }
}
