package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

class TimeReflectionUtilTest {

    @Test
    void ok() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 1);

        assertThat(TimeReflectionUtil.getStartDate(new InputPeriod(startDate, endDate))).isEqualTo("2024-10-01");
        assertThat(TimeReflectionUtil.getStartDate(new InputPeriod(null, endDate))).isNull();

        assertThat(TimeReflectionUtil.getEndDate(new InputPeriod(startDate, endDate))).isEqualTo("2024-11-01");
        assertThat(TimeReflectionUtil.getEndDate(new InputPeriod(startDate, null))).isNull();
    }

    @Test
    void nokWithInvalidName() {
        LocalDate beginDate = LocalDate.of(2024, 10, 1);
        LocalDate endingDate = LocalDate.of(2024, 11, 1);

        InputPeriodInvalidName invalidName = new InputPeriodInvalidName(beginDate, endingDate);

        assertThatIllegalArgumentException().isThrownBy(() -> TimeReflectionUtil.getStartDate(invalidName))
                .withMessageStartingWith("No startDate field with type class java.time.LocalDate was found on class");
        assertThatIllegalArgumentException().isThrownBy(() -> TimeReflectionUtil.getEndDate(invalidName))
                .withMessageStartingWith("No endDate field with type class java.time.LocalDate was found on class");
    }

    @Test
    void nokWithInvalidType() {
        Calendar startDate = new GregorianCalendar(2024, Calendar.OCTOBER, 1);
        Calendar endDate = new GregorianCalendar(2024, Calendar.NOVEMBER, 1);

        InputPeriodInvalidType invalidType = new InputPeriodInvalidType(startDate, endDate);
        assertThatIllegalArgumentException().isThrownBy(() -> TimeReflectionUtil.getStartDate(invalidType))
                .withMessageStartingWith("No startDate field with type class java.time.LocalDate was found on class");
        assertThatIllegalArgumentException().isThrownBy(() -> TimeReflectionUtil.getEndDate(invalidType))
                .withMessageStartingWith("No endDate field with type class java.time.LocalDate was found on class");
    }

    static class InputPeriodInvalidName {
        private final LocalDate beginDate;
        private final LocalDate endingDate;

        InputPeriodInvalidName(LocalDate beginDate, LocalDate endingDate) {
            this.beginDate = beginDate;
            this.endingDate = endingDate;
        }

        public LocalDate getBeginDate() {
            return beginDate;
        }

        public LocalDate getEndingDate() {
            return endingDate;
        }
    }

    static class InputPeriodInvalidType {
        private final Calendar startDate;
        private final Calendar endDate;

        InputPeriodInvalidType(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Calendar getStarDate() {
            return startDate;
        }

        public Calendar getEndDate() {
            return endDate;
        }
    }

}
