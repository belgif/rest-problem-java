package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InputValidationIssue;

class ConstraintViolationUtilTest {

    @Test
    void indexedPropertyPath() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Wrapper target = new Wrapper();
        target.things.add(new Thing("OK"));
        target.things.add(new Thing(null));

        Set<ConstraintViolation<Wrapper>> violations = validator.validate(target);
        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getName()).isEqualTo("things[1].prop");
    }

    static class Wrapper {
        @Valid
        List<Thing> things = new ArrayList<>();
    }

    static class Thing {
        @NotNull
        String prop;

        Thing(String prop) {
            this.prop = prop;
        }
    }

}