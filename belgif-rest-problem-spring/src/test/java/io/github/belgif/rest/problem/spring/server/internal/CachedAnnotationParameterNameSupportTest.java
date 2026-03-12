package io.github.belgif.rest.problem.spring.server.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

class CachedAnnotationParameterNameSupportTest {

    private final CachedAnnotationParameterNameSupport<List<String>> support =
            new CachedAnnotationParameterNameSupport<List<String>>() {
                @Override
                protected List<String> toResult(Stream<String> parameterNames) {
                    return parameterNames.collect(Collectors.toList());
                }
            };

    @Test
    void getParameterNameConstructor() throws Exception {
        assertThat(support.getParameterNames(Tested.class.getDeclaredConstructor(String.class, Long.class)))
                .containsExactly("name", "other");
    }

    @Test
    void getParameterNamesMethod() throws Exception {
        assertThat(
                support.getParameterNames(Tested.class.getDeclaredMethod("plainMethod", String.class, Long.class)))
                        .containsExactly("name", "other");
    }

    @ParameterizedTest
    @ValueSource(
            strings = { "requestParam", "pathVariable", "requestHeader", "cookieValue", "matrixVariable", "fallback" })
    void getParameterNamesMethodWithJaxRsAnnotation(String method) throws Exception {
        assertThat(support.getParameterNames(Tested.class.getDeclaredMethod(method, String.class)))
                .containsExactly("name");
    }

    static class Tested {
        Tested(String name, Long other) {
        }

        void plainMethod(String name, Long other) {
        }

        void requestParam(@RequestParam("name") String x) {
        }

        void pathVariable(@PathVariable("name") String x) {
        }

        void requestHeader(@RequestHeader("name") String x) {
        }

        void cookieValue(@CookieValue("name") String x) {
        }

        void matrixVariable(@MatrixVariable("name") String x) {
        }

        void fallback(@RequestParam String name) {
        }

    }

}
