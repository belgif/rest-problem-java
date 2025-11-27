package io.github.belgif.rest.problem.spring.internal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.belgif.rest.problem.api.InEnum;

class DetermineSourceUtilTest {

    interface Service {
        void requestParam(@RequestParam String requestParam);

        void pathVariable(@PathVariable String pathVariable);

        void requestHeader(@RequestHeader String requestHeader);

        void requestBody(@RequestBody String requestBody);

        void cookieValue(@CookieValue String cookieValue);

        void matrixVariable(@MatrixVariable String matrixVariable);
    }

    private static Stream<Arguments> serviceMethodToInEnum() {
        return Stream.of(
                Arguments.of("requestParam", InEnum.QUERY),
                Arguments.of("pathVariable", InEnum.PATH),
                Arguments.of("requestHeader", InEnum.HEADER),
                Arguments.of("requestBody", InEnum.BODY),
                Arguments.of("cookieValue", InEnum.HEADER),
                Arguments.of("matrixVariable", InEnum.PATH));
    }

    @ParameterizedTest
    @MethodSource("serviceMethodToInEnum")
    void parameter(String serviceMethod, InEnum inEnum) throws Exception {
        assertThat(DetermineSourceUtil.determineSource(
                Service.class.getMethod(serviceMethod, String.class).getParameters()[0]))
                        .isEqualTo(inEnum);
    }

    @ParameterizedTest
    @MethodSource("serviceMethodToInEnum")
    void methodAndParameterIndex(String serviceMethod, InEnum inEnum) throws Exception {
        assertThat(DetermineSourceUtil.determineSource(
                Service.class.getMethod(serviceMethod, String.class), 0))
                        .isEqualTo(inEnum);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("serviceMethodToInEnum")
    void constraintViolation(String serviceMethod, InEnum inEnum) {
        ConstraintViolation<Service> constraintViolation = mock(ConstraintViolation.class);
        when(constraintViolation.getRootBeanClass()).thenReturn(Service.class);
        assertThat(DetermineSourceUtil.determineSource(constraintViolation,
                new LinkedList<>(Collections.singletonList(NodeImpl.createParameterNode(null, null, 0))),
                NodeImpl.createMethodNode(serviceMethod, null, new Class[] { String.class })))
                        .isEqualTo(inEnum);
    }

    @Test
    void constraintViolationParameterNoMethodNode() {
        assertThat(DetermineSourceUtil.determineSource(mock(ConstraintViolation.class),
                new LinkedList<>(Collections.singletonList(NodeImpl.createParameterNode(null, null, 0))), null))
                        .isEqualTo(InEnum.QUERY);
    }

    @Test
    void constraintViolationNoParameter() {
        assertThat(DetermineSourceUtil.determineSource(mock(ConstraintViolation.class),
                new LinkedList<>(Collections.singletonList(NodeImpl.createPropertyNode(null, null))), null))
                        .isEqualTo(InEnum.BODY);
    }

    @SuppressWarnings("unchecked")
    @Test
    void constraintViolationNoSuchMethod() {
        ConstraintViolation<Service> constraintViolation = mock(ConstraintViolation.class);
        when(constraintViolation.getRootBeanClass()).thenReturn(Service.class);
        assertThatIllegalStateException().isThrownBy(() -> DetermineSourceUtil.determineSource(constraintViolation,
                new LinkedList<>(Collections.singletonList(NodeImpl.createParameterNode(null, null, 0))),
                NodeImpl.createMethodNode("noSuchMethod", null, new Class[] { String.class })));
    }

    @ParameterizedTest
    @MethodSource("serviceMethodToInEnum")
    void servletWebRequestParameter(String serviceMethod, InEnum inEnum) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("handler",
                new HandlerMethod("service", Service.class.getMethod(serviceMethod, String.class)));

        assertThat(DetermineSourceUtil.determineSource(new ServletWebRequest(request), serviceMethod))
                .isEqualTo(inEnum);
    }

    @Test
    void servletWebRequestParameterNoHandlerMethod() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThat(DetermineSourceUtil.determineSource(new ServletWebRequest(request), "param"))
                .isEqualTo(InEnum.QUERY);
    }

}
