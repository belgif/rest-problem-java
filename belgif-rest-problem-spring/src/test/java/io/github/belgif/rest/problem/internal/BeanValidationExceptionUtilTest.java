package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class BeanValidationExceptionUtilTest {

    @SuppressWarnings("resource")
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    @Test
    void constraintViolationBodyProperty() {
        Body target = new Body();
        target.value = 10;

        Set<ConstraintViolation<Body>> violations = validator.validate(target);
        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationNestedBodyProperty() {
        Body target = new Body();
        target.nested.add(new Nested("OK"));
        target.nested.add(new Nested(null));

        Set<ConstraintViolation<Body>> violations = validator.validate(target);
        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("nested[1].prop");
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("must not be null");
    }

    @Test
    void constraintViolationQueryParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("queryParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationPathParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("pathParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.PATH);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationHeaderParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("headerParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationNoAnnotationParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("noAnnotationParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationParamFromSuperClass() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("paramFromSuperClass", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void constraintViolationParamFromInterface() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("paramFromInterface", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void fieldError() {
        InputValidationIssue issue =
                BeanValidationExceptionUtil.convertToInputValidationIssue(new FieldError("object", "field", "value",
                        false, null, null, "detail"), InEnum.QUERY);
        assertThat(issue.getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("field");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(issue.getDetail()).isEqualTo("detail");
    }

    interface Interface {
        Object paramFromInterface(@RequestParam("value") @Max(5) int value);
    }

    static class SuperClass {
        public Object paramFromSuperClass(@RequestParam("value") @Max(5) int value) {
            return null;
        }
    }

    static class Resource extends SuperClass implements Interface {
        public Object queryParam(@RequestParam("value") @Max(5) int value) {
            return null;
        }

        public Object pathParam(@PathVariable("value") @Max(5) int value) {
            return null;
        }

        public Object headerParam(@RequestHeader("value") @Max(5) int value) {
            return null;
        }

        public Object noAnnotationParam(@Max(5) int value) {
            return null;
        }

        @Override
        public Object paramFromSuperClass(int value) {
            return super.paramFromSuperClass(value);
        }

        @Override
        public Object paramFromInterface(int value) {
            return null;
        }
    }

    static class Body {
        @Valid
        List<Nested> nested = new ArrayList<>();
        @Max(5)
        Integer value;
    }

    static class Nested {
        @NotNull
        String prop;

        Nested(String prop) {
            this.prop = prop;
        }
    }

}
