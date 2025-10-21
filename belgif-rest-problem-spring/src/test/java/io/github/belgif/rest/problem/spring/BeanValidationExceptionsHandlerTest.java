package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import jakarta.validation.ConstraintViolationException;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Problem;

class BeanValidationExceptionsHandlerTest {

    interface Service {
        void requestBody(@RequestBody Object requestBody);

        void requestParam(@RequestParam Integer requestParam);
    }

    private final BeanValidationExceptionsHandler handler = new BeanValidationExceptionsHandler();

    @Test
    void handleConstraintViolationException() {
        ResponseEntity<Problem> entity = handler.handleConstraintViolationException(
                new ConstraintViolationException(new LinkedHashSet<>(Arrays.asList(
                        ConstraintViolationImpl.forParameterValidation(null, null, null, null, null, null, null,
                                null, PathImpl.createPathFromString("second"), null, null, null),
                        ConstraintViolationImpl.forParameterValidation(null, null, null, null, null, null, null,
                                null, PathImpl.createPathFromString("first"), null, null, null)))));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(2);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("first");
            assertThat(problem.getIssues().get(1).getName()).isEqualTo("second");
        });
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("first", "firstValue");
        map.put("second", "secondValue");
        MapBindingResult bindingResult = new MapBindingResult(map, "map");
        bindingResult.rejectValue("first", "error", "firstDetail");
        bindingResult.rejectValue("second", "error", "secondDetail");
        ResponseEntity<Problem> entity = handler.handleMethodArgumentNotValidException(
                new MethodArgumentNotValidException(MethodParameter.forParameter(
                        Service.class.getMethod("requestBody", Object.class).getParameters()[0]),
                        bindingResult));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(2);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("first");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("firstValue");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("firstDetail");
            assertThat(problem.getIssues().get(1).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(1).getName()).isEqualTo("second");
            assertThat(problem.getIssues().get(1).getValue()).isEqualTo("secondValue");
            assertThat(problem.getIssues().get(1).getDetail()).isEqualTo("secondDetail");
        });
    }

    @Test
    void handleBindException() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("first", "firstValue");
        map.put("second", "secondValue");
        MapBindingResult bindingResult = new MapBindingResult(map, "map");
        bindingResult.rejectValue("first", "error", "firstDetail");
        bindingResult.rejectValue("second", "error", "secondDetail");
        ResponseEntity<Problem> entity = handler.handleBindException(new BindException(bindingResult),
                new ServletWebRequest(new MockHttpServletRequest()));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(2);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("first");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("firstValue");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("firstDetail");
            assertThat(problem.getIssues().get(1).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(1).getName()).isEqualTo("second");
            assertThat(problem.getIssues().get(1).getValue()).isEqualTo("secondValue");
            assertThat(problem.getIssues().get(1).getDetail()).isEqualTo("secondDetail");
        });
    }

    @Test
    void handleMethodArgumentTypeMismatchException() throws Exception {
        ResponseEntity<Problem> entity = handler.handleMethodArgumentTypeMismatchException(
                new MethodArgumentTypeMismatchException("value", Integer.class, "requestParam",
                        MethodParameter.forParameter(
                                Service.class.getMethod("requestParam", Integer.class).getParameters()[0]),
                        new RuntimeException()));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("requestParam");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("value");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("requestParam should be of type Integer");
        });
    }

    @Test
    void handleMethodArgumentTypeMismatchExceptionNoRequiredType() throws Exception {
        ResponseEntity<Problem> entity = handler.handleMethodArgumentTypeMismatchException(
                new MethodArgumentTypeMismatchException("value", null, "requestParam",
                        MethodParameter.forParameter(
                                Service.class.getMethod("requestParam", Integer.class).getParameters()[0]),
                        new RuntimeException()));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("requestParam");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("value");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("requestParam of incorrect type");
        });
    }

}
