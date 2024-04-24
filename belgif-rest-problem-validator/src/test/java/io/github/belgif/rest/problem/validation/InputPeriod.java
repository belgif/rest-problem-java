package io.github.belgif.rest.problem.validation;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Class to Simulate Period and PeriodOpenEnd types from Belgif
 */
class InputPeriod implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;

    public InputPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
