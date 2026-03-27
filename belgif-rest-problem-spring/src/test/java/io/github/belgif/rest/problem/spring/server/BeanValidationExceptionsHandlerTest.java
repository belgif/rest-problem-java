package io.github.belgif.rest.problem.spring.server;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;

class BeanValidationExceptionsHandlerTest {

    interface Service {
        void requestBody(@RequestBody Object requestBody);

        void requestParam(@RequestParam("requestParam") Integer requestParam);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("requestParam");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("value");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("requestParam of incorrect type");
        });
    }

    @Test
    void handleHandlerMethodValidationException() throws Exception {
        Method method = Service.class.getMethod("requestParam", Integer.class);
        MethodParameter parameter = MethodParameter.forParameter(method.getParameters()[0]);
        HandlerMethodValidationException exception =
                new HandlerMethodValidationException(
                        MethodValidationResult.create("test", method,
                                List.of(new ParameterValidationResult(parameter, "value",
                                        List.of(new DefaultMessageSourceResolvable(null,
                                                "requestParam of incorrect type")),
                                        null, null, null, null))));
        ResponseEntity<Problem> entity = handler.handleHandlerMethodValidationException(exception);
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType())
                    .isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("requestParam");
            assertThat(problem.getIssues().get(0).getValue()).isEqualTo("value");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("requestParam of incorrect type");
        });
    }

    @Test
    void handlerMethodValidationExceptionVisitorCookieValue() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        CookieValue cookieValue = mock(CookieValue.class);
        when(cookieValue.value()).thenReturn("name");
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.cookieValue(cookieValue, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorMatrixVariable() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        MatrixVariable matrixVariable = mock(MatrixVariable.class);
        when(matrixVariable.value()).thenReturn("name");
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.matrixVariable(matrixVariable, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.PATH);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorModelAttribute() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        ModelAttribute modelAttribute = mock(ModelAttribute.class);
        when(modelAttribute.value()).thenReturn("name");
        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        when(parameterErrors.getArgument()).thenReturn("value");
        when(parameterErrors.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.modelAttribute(modelAttribute, parameterErrors);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorPathVariable() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        PathVariable pathVariable = mock(PathVariable.class);
        when(pathVariable.value()).thenReturn("name");
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.pathVariable(pathVariable, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.PATH);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorRequestBody() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        RequestBody requestBody = mock(RequestBody.class);
        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        when(parameterErrors.getArgument()).thenReturn("value");
        when(parameterErrors.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.requestBody(requestBody, parameterErrors);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(issues.get(0).getName()).isNull();
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorRequestBodyValidationResult() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        RequestBody requestBody = mock(RequestBody.class);
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.requestBodyValidationResult(requestBody, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(issues.get(0).getName()).isNull();
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorRequestHeader() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        RequestHeader requestHeader = mock(RequestHeader.class);
        when(requestHeader.value()).thenReturn("name");
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.requestHeader(requestHeader, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorRequestParam() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        RequestParam requestParam = mock(RequestParam.class);
        when(requestParam.value()).thenReturn("name");
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.requestParam(requestParam, parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorRequestPart() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        RequestPart requestPart = mock(RequestPart.class);
        when(requestPart.value()).thenReturn("name");
        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        when(parameterErrors.getArgument()).thenReturn("value");
        when(parameterErrors.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.requestPart(requestPart, parameterErrors);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(issues.get(0).getName()).isEqualTo("name");
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

    @Test
    void handlerMethodValidationExceptionVisitorOther() {
        BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor visitor =
                new BeanValidationExceptionsHandler.HandlerMethodValidationExceptionVisitor();
        ParameterValidationResult parameterValidationResult = mock(ParameterValidationResult.class);
        when(parameterValidationResult.getArgument()).thenReturn("value");
        when(parameterValidationResult.getResolvableErrors())
                .thenReturn(List.of(new DefaultMessageSourceResolvable(null, "message")));

        visitor.other(parameterValidationResult);

        List<InputValidationIssue> issues = visitor.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getType()).isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
        assertThat(issues.get(0).getIn()).isNull();
        assertThat(issues.get(0).getName()).isNull();
        assertThat(issues.get(0).getValue()).isEqualTo("value");
        assertThat(issues.get(0).getDetail()).isEqualTo("message");
    }

}
