package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;

import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.ParamConverter;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class AbstractInputParamConverterProviderTest {

    static class ExampleConverter extends AbstractInputParamConverterProvider<String> {

        ExampleConverter() {
            super(String.class);
        }

        @Override
        protected String fromString(InEnum in, String name, String value) {
            if ("invalid".equals(value)) {
                throw new BadRequestProblem(InputValidationIssues.invalidInput(in, name, value, "test"));
            }
            return value;
        }
    }

    private final ExampleConverter provider = new ExampleConverter();

    @Test
    void wrongType() {
        assertThat(provider.getConverter(Integer.class, null, new Annotation[] {})).isNull();
    }

    @Test
    void withoutParameterAnnotation() {
        ParamConverter<String> converter = provider.getConverter(String.class, null, new Annotation[] {});
        assertThat(converter.fromString("test")).isEqualTo("test");
        assertThat(converter.toString("test")).isEqualTo("test");
        assertThatExceptionOfType(BadRequestProblem.class).isThrownBy(() -> converter.fromString("invalid"))
                .satisfies(e -> {
                    assertThat(e.getIssues().get(0).getIn()).isNull();
                    assertThat(e.getIssues().get(0).getName()).isNull();
                });
    }

    @Test
    void withParameterAnnotation() throws Exception {
        class Resource {
            void method(@QueryParam("param") String param) {
            }
        }
        ParamConverter<String> converter = provider.getConverter(String.class, null,
                Resource.class.getDeclaredMethod("method", String.class).getParameters()[0].getDeclaredAnnotations());
        assertThat(converter.fromString("test")).isEqualTo("test");
        assertThat(converter.toString("test")).isEqualTo("test");
        assertThatExceptionOfType(BadRequestProblem.class).isThrownBy(() -> converter.fromString("invalid"))
                .satisfies(e -> {
                    assertThat(e.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
                    assertThat(e.getIssues().get(0).getName()).isEqualTo("param");
                });
    }

}
