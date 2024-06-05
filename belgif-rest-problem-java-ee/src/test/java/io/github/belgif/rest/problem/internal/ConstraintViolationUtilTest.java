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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class ConstraintViolationUtilTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void bodyProperty() {
        Body target = new Body();
        target.value = 10;

        Set<ConstraintViolation<Body>> violations = validator.validate(target);
        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void nestedBodyProperty() {
        Body target = new Body();
        target.nested.add(new Nested("OK"));
        target.nested.add(new Nested(null));

        Set<ConstraintViolation<Body>> violations = validator.validate(target);
        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("nested[1].prop");
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("must not be null");
    }

    @Test
    void queryParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("queryParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void pathParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("pathParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.PATH);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void headerParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("headerParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void noAnnotationParam() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("noAnnotationParam", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void paramFromSuperClass() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("paramFromSuperClass", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    @Test
    void paramFromInterface() throws Exception {
        Set<ConstraintViolation<Resource>> violations =
                validator.forExecutables().validateParameters(new Resource(),
                        Resource.class.getMethod("paramFromInterface", int.class), new Object[] { 10 });

        assertThat(violations).hasSize(1);

        InputValidationIssue issue =
                ConstraintViolationUtil.convertToInputValidationIssue(violations.iterator().next());
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("value");
        assertThat(issue.getValue()).isEqualTo(10);
        assertThat(issue.getDetail()).isEqualTo("must be less than or equal to 5");
    }

    interface Interface {
        Response paramFromInterface(@QueryParam("value") @Max(5) int value);
    }

    static class SuperClass {
        public Response paramFromSuperClass(@QueryParam("value") @Max(5) int value) {
            return null;
        }
    }

    static class Resource extends SuperClass implements Interface {
        public Response queryParam(@QueryParam("value") @Max(5) int value) {
            return null;
        }

        public Response pathParam(@PathParam("value") @Max(5) int value) {
            return null;
        }

        public Response headerParam(@HeaderParam("value") @Max(5) int value) {
            return null;
        }

        public Response noAnnotationParam(@Max(5) int value) {
            return null;
        }

        @Override
        public Response paramFromSuperClass(int value) {
            return super.paramFromSuperClass(value);
        }

        @Override
        public Response paramFromInterface(int value) {
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
