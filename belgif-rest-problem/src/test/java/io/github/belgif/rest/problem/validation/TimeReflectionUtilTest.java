package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

        assertThrows(IllegalArgumentException.class, () -> TimeReflectionUtil.getStartDate(invalidName),
                "Should have thrown exception");
        assertThrows(IllegalArgumentException.class, () -> TimeReflectionUtil.getEndDate(invalidName),
                "Should have thrown exception");
    }

    @Test
    void nokWithInvalidType() {

        Calendar startDate = new GregorianCalendar(2024, Calendar.OCTOBER, 1);
        Calendar endDate = new GregorianCalendar(2024, Calendar.NOVEMBER, 1);

        InputPeriodInvalidType invalidType = new InputPeriodInvalidType(startDate, endDate);
        assertThrows(IllegalArgumentException.class, () -> TimeReflectionUtil.getStartDate(invalidType),
                "Should have thrown exception");
        assertThrows(IllegalArgumentException.class, () -> TimeReflectionUtil.getEndDate(invalidType),
                "Should have thrown exception");
    }

    class InputPeriodInvalidName {
        private LocalDate beginDate;
        private LocalDate endingDate;

        InputPeriodInvalidName(LocalDate beginDate, LocalDate endingDate) {
            this.beginDate = beginDate;
            this.endingDate = endingDate;
        }

    }

    class InputPeriodInvalidType {
        private Calendar startDate;
        private Calendar endDate;

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
