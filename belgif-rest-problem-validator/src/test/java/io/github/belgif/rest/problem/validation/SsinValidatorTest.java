package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class SsinValidatorTest {

    @Test
    void ok() {
        assertValidSsin("23292805780");
    }

    @Test
    void okIncompleteDate() {
        assertValidSsin("81050003552");
    }

    @Test
    void okBis() {
        // bis with month == 20
        assertValidSsin("92200006963");
        // bis with month == 32
        assertValidSsin("92321506985");
        // bis with month == 40
        assertValidSsin("92400006909");
        // bis with month == 52
        assertValidSsin("92521506931");
        // bis with month >20 & <32
        assertValidSsin("70252501507");
        // bis with month >40 & <52
        assertValidSsin("92471506993");
        // bis 2k
        assertValidSsin("19282416134");
        // counter == 999 with BIS
        assertValidSsin("80240199943");
    }

    @Test
    void okNr() {
        // nr with month == 0
        assertValidSsin("81000003517");
        // nr with month == 12
        assertValidSsin("81120103571");
        // nr with month > 0 && month < 12
        assertValidSsin("81050103522");
        // nr 2k
        assertValidSsin("19082416188");
    }

    @Test
    void okCurrentDate() {
        // birth date is today
        assertValidSsin(createSsinFromDate(LocalDate.now()));
    }

    @Test
    void nokBirthDateInFuture() {
        // birth date is tomorrow
        assertInvalidSsin(createSsinFromDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void nokUnspecifiedMonth() {
        assertInvalidSsin("81001202357");
    }

    @Test
    void nokMonthOutOfRange() {
        assertInvalidSsin("81130103578");
    }

    @Test
    void nokDateOutOfRange() {
        assertInvalidSsin("81043103522");
    }

    @Test
    void nokDateOutOfRangeForMonth() {
        assertInvalidSsin("81023003522");
    }

    @Test
    void nokInvalidDate() {
        assertInvalidSsin("81022903534");
    }

    @Test
    void nokNrWithCounter0() {
        assertInvalidSsin("47060200096");
    }

    @Test
    void nokNrWithCounter999() {
        assertInvalidSsin("99020299964");
    }

    @Test
    void nokChecksum() {
        assertInvalidSsin("81050103521");
    }

    private void assertValidSsin(String ssin) {
        assertThat(new SsinValidator(Input.body("test", ssin)).validate()).isEmpty();
    }

    private void assertInvalidSsin(String ssin) {
        assertThat(new SsinValidator(Input.body("test", ssin)).validate())
                .contains(InputValidationIssues.invalidSsin(BODY, "test", ssin));
    }

    private static String createSsinFromDate(LocalDate date) {
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
